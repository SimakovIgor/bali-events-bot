package com.balievent.telegrambot.service.support;

import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd_MM_yyyy");
    private final EventRepository eventRepository;

    private static String formatMessageForEventsGroupedByDay(final Map<LocalDate, List<Event>> eventMap) {
        final Map<LocalDate, List<Event>> reverseSortedMap = new TreeMap<>(eventMap);
        final StringBuilder stringBuilder = new StringBuilder();
        reverseSortedMap.forEach((key, value) ->
            stringBuilder.append("/").append(key.format(DATE_TIME_FORMATTER))
                .append(" : ")
                .append(value.size())
                .append(" events")
                .append("\n"));

        if (stringBuilder.isEmpty()) {
            stringBuilder.append("No events");
        }

        return stringBuilder.toString();
    }

    /***
     * Получим строку в которой будет Список событий по дням на указанный месяц
     *
     * @param localDate - дата для запроса пользователя получается из "FEBRUARY (02.2024)"
     * @param dayStart  - начальный день запроса. Минимум может быть: 1
     * @param dayFinish - последний дней запроса. Максимум может быть: localDate.lengthOfMonth()
     * @return String   - текст сообщеня
     */
    public String getMessageWithEventsGroupedByDay(final LocalDate localDate, final int dayStart, final int dayFinish) {
        final LocalDateTime start = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), 1, 0, 0);
        final LocalDateTime end = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth(), 23, 59);

        // запрос к базе данных за указанный месяц
        final Map<LocalDate, List<Event>> eventMap = eventRepository.findEventsByStartDateBetween(start, end)
            .stream()
            .collect(Collectors.groupingBy(event -> event.getStartDate().toLocalDate()));

        // форматируем записи
        final String formattedMessage = formatMessageForEventsGroupedByDay(eventMap);

        if (dayStart > 0) {
            return getFirstEvents(formattedMessage, dayStart); // получаем первые пять записей
        }

        if (dayFinish > 0) {
            return getNextEvents(formattedMessage, dayFinish); // получаем от шестой записи и далее
        }
        return formattedMessage; // получим записи за весь месяц

    }

    public List<Event> findEvents(final int day, final int month, final int year) {
        final LocalDateTime from = LocalDateTime.of(year, month, day, 0, 0);
        final LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59);
        return eventRepository.findEventsByStartDateBetween(from, end);
    }

    private static String getFirstEvents(final String formattedMessage, final int dayStart) {
        final List<String> lines = Arrays.stream(formattedMessage.split("\n"))
                                        .collect(Collectors.toList());
        final List<String> firstFiveLines = lines.subList(0, Math.min(lines.size(), dayStart));
        return String.join("\n", firstFiveLines);
    }

    private static String getNextEvents(final String formattedMessage, final int dayFinish) {
        final List<String> lines = Arrays.stream(formattedMessage.split("\n"))
                                        .collect(Collectors.toList());
        final List<String> nextEvents = lines.subList(dayFinish, lines.size()); // Начинаем с шестой строки
        return String.join("\n", nextEvents);
    }
}
