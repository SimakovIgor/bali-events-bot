package com.example.telegrambot.service.handler.textmessage;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.model.entity.Event;
import com.example.telegrambot.service.storage.CalendarDataStorage;
import com.example.telegrambot.service.support.EventService;
import com.example.telegrambot.util.CommonUtil;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DateSelectedHandler implements TextMessageHandler {
    private final EventService eventService;
    private final CalendarDataStorage calendarDataStorage;

    @Override
    public SendMessage handleTextMessage(final Update update) {
        final LocalDate localDate = calendarDataStorage.updateWithSelectedDate(update);
        final int day = localDate.getDayOfMonth();
        final int month = localDate.getMonthValue();
        final int year = localDate.getYear();

        final String eventListToday = getBriefEventsForToday(day, month, year);

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(String.format("%s %02d.%02d.%d%n%s", MyConstants.LIST_OF_EVENTS_ON, day, month, year, eventListToday))
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear()))
            .build();
    }

    private String getBriefEventsForToday(final int day, final int month, final int year) {
        final List<Event> eventList = eventService.findEvents(day, month, year);

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            stringBuilder.append(i + 1).append(". ")
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n");
        }
        return stringBuilder.toString();
    }
}
