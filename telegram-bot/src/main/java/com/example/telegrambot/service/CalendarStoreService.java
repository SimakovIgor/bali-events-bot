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

    public LocalDate putOrUpdate(Update update) {
        Long chatId = update.getMessage().getChatId();
        LocalDate dateToStore = calendarStore.containsKey(chatId)
                                ? LocalDate.parse(update.getMessage().getText(), DATE_TIME_FORMATTER)
                                : LocalDate.now();

        calendarStore.put(chatId, dateToStore);

        return calendarStore.get(chatId);
    }

    public LocalDate get(Update update) {
        return calendarStore.get(update.getMessage().getChatId());
    }

}
