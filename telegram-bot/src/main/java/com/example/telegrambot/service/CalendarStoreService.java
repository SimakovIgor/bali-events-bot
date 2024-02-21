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

    public LocalDate update(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        final String messageText = update.getMessage().getText();
        final LocalDate currentLocalDate = calendarStore.get(chatId);

        final String text = DateUtil.isContainsTextMonth(messageText)
                            ? DateUtil.convertToLocalDateString(messageText, currentLocalDate)
                            : messageText;

        final LocalDate dateToStore = LocalDate.parse(text, DATE_TIME_FORMATTER);

        calendarStore.put(chatId, dateToStore);

        return dateToStore;
    }

    public void put(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        calendarStore.put(chatId, LocalDate.now());
    }

    public LocalDate get(final Update update) {
        return calendarStore.get(update.getMessage().getChatId());
    }

}
