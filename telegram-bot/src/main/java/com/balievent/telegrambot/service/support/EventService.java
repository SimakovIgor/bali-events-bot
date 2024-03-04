package com.balievent.telegrambot.service.support;

import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.repository.EventRepository;
import com.balievent.telegrambot.service.storage.UserDataStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd_MM_yyyy");
    public static final int SHOW_ROW_COUNT = 5;
    public static final Comparator<Map.Entry<LocalDate, List<Event>>> COMPARING_BY_LOCAL_DATE = Map.Entry.comparingByKey();
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
    public String getMessageWithEventsGroupedByDay(final Long chatId,
                                                    final LocalDate localDate,
                                                    final int dayStart,
                                                    final int dayFinish) {
        final Map<LocalDate, List<Event>> eventsAndGroupByDay = getEventsAndGroupByDay(localDate, dayStart, dayFinish);

        final Map<LocalDate, List<Event>> eventMap = eventsAndGroupByDay
            .entrySet()
            .stream()
            .sorted(COMPARING_BY_LOCAL_DATE)
            .limit(SHOW_ROW_COUNT)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return formatMessageForEventsGroupedByDay(eventMap);
    }

    /***
     * Получим строку в которой будет Список событий по дням на указанный месяц
     *
     * @param localDate - дата для запроса пользователя получается из "FEBRUARY (02.2024)"
     * @param dayStart  - начальный день запроса. Минимум может быть: 1
     * @param dayFinish - последний дней запроса. Максимум может быть: localDate.lengthOfMonth()
     * @return String   - текст сообщеня
     */
    public String getMessageWithEventsGroupedByDayFull(final LocalDate localDate,
                                                       final int dayStart,
                                                       final int dayFinish) {
        final Map<LocalDate, List<Event>> eventMap = getEventsAndGroupByDay(localDate, dayStart, dayFinish)
            .entrySet()
            .stream()
            .sorted(COMPARING_BY_LOCAL_DATE)
            .skip(SHOW_ROW_COUNT)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return formatMessageForEventsGroupedByDay(eventMap);
    }

    private Map<LocalDate, List<Event>> getEventsAndGroupByDay(final LocalDate localDate,
                                                               final int dayStart,
                                                               final int dayFinish) {
        final LocalDateTime start = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), dayStart, 0, 0);
        final LocalDateTime end = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), dayFinish, 23, 59);
        return eventRepository.findEventsByStartDateBetween(start, end)
            .stream()
            .collect(Collectors.groupingBy(event -> event.getStartDate().toLocalDate()));
    }

    public List<Event> findEvents(final int day, final int month, final int year) {
        final LocalDateTime from = LocalDateTime.of(year, month, day, 0, 0);
        final LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59);
        return eventRepository.findEventsByStartDateBetween(from, end);
    }

    public List<Event> findEvents(final LocalDate localDate, final int page, final int pageSize) {
        final int day = localDate.getDayOfMonth();
        final int month = localDate.getMonthValue();
        final int year = localDate.getYear();

        final LocalDateTime from = LocalDateTime.of(year, month, day, 0, 0);
        final LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59);

        final Pageable pageable = PageRequest.of(page, pageSize);
        return eventRepository.findEventsByStartDateBetween(from, end, pageable)
            .getContent();
    }

}
