package com.example.telegrambot.service;

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

    public LocalDate putOrUpdate(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        final LocalDate dateToStore = calendarStore.containsKey(chatId)
                                      ? LocalDate.parse(update.getMessage().getText(), DATE_TIME_FORMATTER)
                                      : LocalDate.now();

        calendarStore.put(chatId, dateToStore);

        return calendarStore.get(chatId);
    }

    public void put(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        calendarStore.put(chatId, LocalDate.now());
    }

    public LocalDate get(final Update update) {
        return calendarStore.get(update.getMessage().getChatId());
    }

}
