package com.example.telegrambot.service;

import com.example.telegrambot.contant.Constants;
import com.example.telegrambot.model.entity.Event;
import com.example.telegrambot.repository.EventRepository;
import com.example.telegrambot.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarProcessService {

    private final EventRepository eventRepository;

    public SendMessage process(final Update update, final LocalDate localDate) {
        final int day = localDate.getDayOfMonth();
        final int month = localDate.getMonthValue();
        final int year = localDate.getYear();

        final String eventListToday = findEventListToday(day, month, year);

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(String.format("%s %02d.%02d.%d %n%n%n %s", Constants.LIST_OF_EVENTS_ON, day, month, year, eventListToday))
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .build();
    }

    private String findEventListToday(final int day, final int month, final int year) {
        final LocalDateTime from = LocalDateTime.of(year, month, day, 0, 0);
        final LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59);

        final List<Event> eventList = eventRepository.findEventsByStartDateBetween(from, end);

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
