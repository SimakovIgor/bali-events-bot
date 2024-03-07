package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.service.storage.UserDataService;
import com.balievent.telegrambot.service.support.EventService;
import com.balievent.telegrambot.service.support.MessageBuilder;
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
    private final UserDataService userDataService;

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.DATE_SELECTED;
    }

    @Override
    public SendMessage handle(final Update update) {
        final LocalDate eventsDateFor = userDataService.updateDataStore(update, false).getCalendarDate();
        final Long chatId = update.getMessage().getChatId();

        final int currentPage = 1;
        userDataService.setCurrentPage(chatId, currentPage);

        final List<Event> eventList = eventService.findEvents(eventsDateFor, currentPage - 1, Settings.PAGE_SIZE);
        final String eventsBriefMessage = MessageBuilder.buildBriefEventsMessage(currentPage, eventList);
        final int eventCount = eventService.countEvents(eventsDateFor);
        final int pageCount = (eventCount + Settings.PAGE_SIZE - 1) / Settings.PAGE_SIZE;

        userDataService.setPageCount(chatId, pageCount);

        final ReplyKeyboard replyKeyboard = pageCount == 1
                                            ? KeyboardUtil.setCalendar(eventsDateFor.getMonthValue(), eventsDateFor.getYear())
                                            : KeyboardUtil.getPaginationKeyboard(currentPage, pageCount);

        final String displayDate = eventsDateFor.format(Settings.PRINT_DATE_TIME_FORMATTER);
        return SendMessage.builder()
            .chatId(chatId)
            .text(TgBotConstants.LIST_OF_EVENTS_ON.formatted(displayDate, eventsBriefMessage))
            .parseMode(ParseMode.HTML)
            .replyMarkup(replyKeyboard)
            .disableWebPagePreview(true)
            .build();

    }

}
