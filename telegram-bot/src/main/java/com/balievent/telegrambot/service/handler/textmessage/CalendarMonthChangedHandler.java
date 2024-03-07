package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.service.storage.UserDataService;
import com.balievent.telegrambot.service.support.EventService;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CalendarMonthChangedHandler implements TextMessageHandler {

    private final EventService eventService;
    private final UserDataService userDataService;

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.CALENDAR_MONTH_CHANGED;
    }

    @Override
    public SendMessage handle(final Update update) {
        final LocalDate localDate = userDataService.updateDataStore(update, true).getCalendarDate();
        final String messageWithEventsGroupedByDay = eventService.getMessageWithEventsGroupedByDay(localDate, 1, localDate.lengthOfMonth());
        final String displayDate = update.getMessage().getText();

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(TgBotConstants.LIST_OF_EVENTS_ON.formatted(displayDate, messageWithEventsGroupedByDay))
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear()))
            .build();
    }
}
