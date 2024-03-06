package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.service.storage.UserDataStorage;
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
    private final UserDataStorage userDataStorage;

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.CALENDAR_MONTH_CHANGED;
    }

    @Override
    public SendMessage handle(final Update update) {
        final LocalDate localDate = userDataStorage.updateWithCalendarMonthChanged(update);
        final String messageWithEventsGroupedByDay = eventService.getMessageWithEventsGroupedByDay(localDate, 1, localDate.lengthOfMonth());
        final String text = update.getMessage().getText();

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(String.format("%s %s%n%s", TgBotConstants.LIST_OF_EVENTS_ON, text, messageWithEventsGroupedByDay))
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear()))
            .build();
    }
}
