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

    private static String parseSelectedDate(final String text, final LocalDate storedLocalDate) {
        if (DateUtil.isContainsTextMonth(text)) {
            return DateUtil.convertToLocalDateSelected(text, storedLocalDate);
        } else if (text.startsWith("/")) {
            return text.substring(1).replace("_", ".");
        } else {
            return text;
        }
    }

    public LocalDate updateWithSelectedDate(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        final String text = update.getMessage().getText();

        final LocalDate storedLocalDate = calendarStore.get(chatId) == null
                                          ? LocalDate.now()
                                          : calendarStore.get(chatId);

        final String localDateText = parseSelectedDate(text, storedLocalDate);
        final LocalDate dateToStore = LocalDate.parse(localDateText, DATE_TIME_FORMATTER);

        calendarStore.put(chatId, dateToStore);

        return dateToStore;
    }

    public LocalDate updateWithCalendarMonthChanged(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        final String text = update.getMessage().getText();
        final LocalDate storedLocalDate = (calendarStore.get(chatId) != null) ? calendarStore.get(chatId) : LocalDate.now();

        final String localDateText = DateUtil.convertToDateTimeCalendarMonthChanged(text, storedLocalDate);
        final LocalDate localDate = LocalDate.parse(localDateText, DATE_TIME_FORMATTER);

        calendarStore.put(chatId, localDate);
        return localDate;
    }

    public void put(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        calendarStore.put(chatId, LocalDate.now());
    }

    public LocalDate get(final Update update) {
        return calendarStore.get(update.getMessage().getChatId());
    }

}
