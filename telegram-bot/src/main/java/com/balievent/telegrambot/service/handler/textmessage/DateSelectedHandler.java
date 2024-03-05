package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.contant.Settings;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.service.storage.UserDataStorage;
import com.balievent.telegrambot.service.support.EventService;
import com.balievent.telegrambot.util.CommonUtil;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DateSelectedHandler implements TextMessageHandler {
    private final EventService eventService;
    private final UserDataStorage userDataStorage;

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.DATE_SELECTED;
    }

    @Override
    public SendMessage handle(final Update update) {
        final LocalDate localDate = userDataStorage.updateWithSelectedDate(update);
        // сбрасываем страницу при первом выборе даты

        userDataStorage.setPageAndGetUserData(update.getMessage().getChatId(), 0);
        final String eventListToday = getBriefEventsBodyForToday(update.getMessage().getChatId(), localDate);

        final ReplyKeyboard replyKeyboard = isOnlyOnePage(update.getMessage().getChatId())
                                            ? KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear())
                                            : KeyboardUtil.getPaginationKeyboard();

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(String.format("%s %s %n%n%s", MyConstants.LIST_OF_EVENTS_ON, localDate.format(Settings.PRINT_DATE_TIME_FORMATTER), eventListToday))
            .parseMode(ParseMode.HTML)
            .replyMarkup(replyKeyboard)
            .disableWebPagePreview(true)
            .build();
    }

    /**
     * Проверяем, что у нас только одна страница
     *
     * @param chatId - идентификатор чата
     * @return - true, если только одна страница
     */
    private boolean isOnlyOnePage(final Long chatId) {
        if (userDataStorage.getUserData(chatId) == null) {
            return true;
        } else {
            return userDataStorage.getUserData(chatId)
                .getPageCount() == 0;
        }
    }

    private String getBriefEventsBodyForToday(final Long chatId, final LocalDate localDate) {
        final int eventCount = eventService.countEvents(localDate);
        final int pageCount = ((eventCount + Settings.PAGE_SIZE - 1) / Settings.PAGE_SIZE) - 1;
        final StringBuilder stringBuilder = new StringBuilder();
        if (pageCount == -1) { // проверка на то что что записей нет совсем
            userDataStorage.setPageCount(chatId, 0);

            return MyConstants.NO_EVENTS;
        }
        // сохраняем их для текущего пользователя
        userDataStorage.setPageCount(chatId, pageCount);

        final List<Event> eventList = eventService.findEvents(localDate, 0, Settings.PAGE_SIZE);

        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            stringBuilder.append(i + 1).append(". ")
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n");
        }

        if (pageCount > 0) {
            stringBuilder.append("\nAll ")
                .append(1)
                .append("\\")
                .append(pageCount + 1)
                .append(" ")
                .append(MyConstants.PAGES + "\n");
        }

        return stringBuilder.toString();
    }
}
