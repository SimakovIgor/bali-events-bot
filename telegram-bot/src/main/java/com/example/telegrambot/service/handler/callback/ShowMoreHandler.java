package com.example.telegrambot.service.handler.callback;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.model.entity.Event;
import com.example.telegrambot.service.support.EventService;
import com.example.telegrambot.service.storage.MessageDataStorage;
import com.example.telegrambot.util.CommonUtil;
import com.example.telegrambot.util.GetGoogleMapLink;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowMoreHandler implements CallbackQueryHandler {
    private final MessageDataStorage messageDataStorage;
    private final EventService eventService;

    @Override
    public EditMessageText handleCallbackQuery(final Update update, final String showWord) {
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final String callbackData = callbackQuery.getData();

        // Получение идентификатора сообщения из колбэк-данных Пример SHOW_MORE:123
        final String chatIdString = callbackQuery.getMessage().getChatId().toString();
        final Long messageIdFromCallbackData = CommonUtil.getMessageIdFromCallbackData(callbackData);
        final Integer messageId = Integer.parseInt(messageDataStorage.getMessageTimestamp(chatIdString, messageIdFromCallbackData));
        final LocalDate localDate = messageDataStorage.getLocalDate(chatIdString, messageIdFromCallbackData);

        final int day = localDate.getDayOfMonth();
        final int month = localDate.getMonthValue();
        final int year = localDate.getYear();

        String text = "";
        String newCallbackData = "";
        if (showWord.contains(MyConstants.SHOW_MORE)) {
            text = String.format("%s %02d.%02d.%d%n%s", MyConstants.LIST_OF_EVENTS_ON, day, month, year, getDetailedEventsForToday(day, month, year));
            newCallbackData = MyConstants.SHOW_LESS + MyConstants.COLON_MARK + messageIdFromCallbackData;
        } else if (showWord.contains(MyConstants.SHOW_SHORT_MONTH)) {
            text = eventService.getMessageWithEventsGroupedByDay(localDate, 6, localDate.lengthOfMonth());
            newCallbackData = MyConstants.SHOW_SHORT_MONTH + MyConstants.COLON_MARK + messageIdFromCallbackData;
        }

        final Long chatId = callbackQuery.getMessage().getChatId();

        return EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(text)
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.updateButton(newCallbackData))
            .build();
    }

    private String getDetailedEventsForToday(final int day, final int month, final int year) {
        final List<Event> eventList = eventService.findEvents(day, month, year);

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            stringBuilder.append(i + 1).append(". ")
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n")
                .append("Location:")
                .append("\n")
                .append(event.getLocationAddress())
                .append("\n")
                .append("Google Maps:")
                .append(GetGoogleMapLink.getGoogleMap(event.getCoordinates(), event.getCoordinates()))
                .append("\n")
                .append("----------------")
                .append("\n\n");
        }

        return stringBuilder.toString();
    }
}
