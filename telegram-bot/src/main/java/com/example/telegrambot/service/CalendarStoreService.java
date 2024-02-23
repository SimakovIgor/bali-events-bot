package com.example.telegrambot.service;

import com.example.telegrambot.util.DateUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CalendarStoreService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Map<Long, LocalDate> calendarStore = new ConcurrentHashMap<>(100);

    public LocalDate updateWithSelectedDate(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        final String text = update.getMessage().getText();
        final LocalDate storedLocalDate = calendarStore.get(chatId);

        final String localDateText = DateUtil.isContainsTextMonth(text)
                                     ? DateUtil.convertToLocalDateSelected(text, storedLocalDate)
                                     : text;

        final LocalDate dateToStore = LocalDate.parse(localDateText, DATE_TIME_FORMATTER);

        calendarStore.put(chatId, dateToStore);

        return dateToStore;
    }

    public void updateWithCalendarMonthChanged(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        final String text = update.getMessage().getText();
        final LocalDate storedLocalDate = calendarStore.get(chatId);

        final String localDateText = DateUtil.convertToDateTimeCalendarMonthChanged(text, storedLocalDate);
        final LocalDate localDate = LocalDate.parse(localDateText, DATE_TIME_FORMATTER);

        calendarStore.put(chatId, localDate);
    }

    public void put(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        calendarStore.put(chatId, LocalDate.now());
    }

    public LocalDate get(final Update update) {
        return calendarStore.get(update.getMessage().getChatId());
    }

}
