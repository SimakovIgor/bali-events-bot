package com.balievent.telegrambot.service.storage;

import com.balievent.telegrambot.util.DateUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CalendarDataStorage {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Map<Long, LocalDate> calendarStore = new ConcurrentHashMap<>(100);

    /**
     * Updates the calendar store with the selected date or the changed calendar month.
     *
     * @param update         - the update event from Telegram
     * @param isMonthChanged - a flag indicating whether the calendar month has changed
     * @return the updated local date
     */
    private LocalDate updateCalendarStore(final Update update, final boolean isMonthChanged) {
        final Long chatId = update.getMessage().getChatId();
        final String text = update.getMessage().getText();

        final LocalDate storedLocalDate = calendarStore.getOrDefault(chatId, LocalDate.now());

        final String localDateText = isMonthChanged
                                     ? DateUtil.convertToDateTimeCalendarMonthChanged(text, storedLocalDate)
                                     : DateUtil.parseSelectedDate(text, storedLocalDate);

        final LocalDate dateToStore = LocalDate.parse(localDateText, DATE_TIME_FORMATTER);

        calendarStore.put(chatId, dateToStore);

        return dateToStore;
    }

    public LocalDate updateWithSelectedDate(final Update update) {
        return updateCalendarStore(update, false);
    }

    public LocalDate updateWithCalendarMonthChanged(final Update update) {
        return updateCalendarStore(update, true);
    }

    public LocalDate put(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        calendarStore.put(chatId, LocalDate.now());
        return get(update);
    }

    public LocalDate get(final Update update) {
        return calendarStore.get(update.getMessage().getChatId());
    }

}
