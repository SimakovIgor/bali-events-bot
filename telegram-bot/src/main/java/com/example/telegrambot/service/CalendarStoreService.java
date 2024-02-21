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
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // Формат даты
    private final Map<Long, LocalDate> calendarStore = new ConcurrentHashMap<>(100); // глобальный список пользователей и их последня дата запроса

    public LocalDate putOrUpdate(final Update update) {
        final Long chatId = update.getMessage().getChatId();              // идентификатор чата
        String text = update.getMessage().getText();                // сообщение из чата

        if (DateUtil.getMonthNumber(text) > 0) {                    // Проверяем наличие месяца в строке JAN, FEB, MAR, APR, MAY .... "JANUARY (01.2024)"
            text = getLocalDate(chatId, text);                      // Преобразуем строку "15 Jan" или "JANUARY (01.2024)" в "15.01.2024"
        }

        final LocalDate dateToStore = calendarStore.containsKey(chatId)
                                      ? LocalDate.parse(text, DATE_TIME_FORMATTER)
                                      : LocalDate.now();

        calendarStore.put(chatId, dateToStore);

        return calendarStore.get(chatId);
    }

    /**
     * Преобразует строку "15 Jan" или "JANUARY (01.2024)" в "15.01.2024"
     * @param chatId.
     * @param text.
     * @return String "15.01.2024"
     */
    private String getLocalDate(Long chatId, String text) {
        LocalDate dateToStore = calendarStore.containsKey(chatId)   // Получаем дату из данных по пользователю или текущую дату
                                ? LocalDate.parse(calendarStore.get(chatId).format(DATE_TIME_FORMATTER), DATE_TIME_FORMATTER)
                                : LocalDate.now();                  // текущая дата

        // Получаем дату, месяц и год из текущих значений
        int day = dateToStore.getDayOfMonth();                      // Дата
        int month = dateToStore.getMonthValue();                    // Месяц
        int year = dateToStore.getYear();                           // Год

        int monthNumber = DateUtil.getFullMonthNumber(text);        // если есть переход месяца
        if (monthNumber > 0) {                                      //  нажали кнопку перехода на другой месяц
            if (0 < monthNumber && monthNumber < 13) {
                if (month == 12 && monthNumber == 1) {              // переход на следующий год
                    year++;
                } else if (month == 1 && monthNumber == 12) {       // переход на предыдущий год
                    year--;
                }
                month = monthNumber;                                // перехода на другой месяц
            }
        } else {                                                    // иначе нажали день в календаре, и нужно получить число
            int firstTwoDigits = Integer.parseInt(text.substring(0, 2));
            if (firstTwoDigits > 0 && firstTwoDigits < 32) {
                day = firstTwoDigits;                               // устанавливаем день
                month = DateUtil.getMonthNumber(text);              // устанавливаем месяц
            }
        }
        return isValidDate(day, month, year);                       // проверка на правильность даты
    }

    public void put(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        calendarStore.put(chatId, LocalDate.now());
    }

    public LocalDate get(final Update update) {
        return calendarStore.get(update.getMessage().getChatId());
    }

    private String isValidDate(int day, int month, int year) {
        try {
            LocalDate.of(year, month, day);
            return String.format("%02d.%02d.%d", day, month, year);
        } catch (java.time.DateTimeException e) {
            day--;
            if (day > 0) {
                return String.format("%02d.%02d.%d", day, month, year);
            } else {
                LocalDate currentDate = LocalDate.now();
                return String.format("%02d.%02d.%d", currentDate.getDayOfMonth(), currentDate.getMonthValue(), currentDate.getYear());
            }
        }
    }
}
