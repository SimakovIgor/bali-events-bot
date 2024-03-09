package com.balievent.telegrambot.service.support;

import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.repository.EventRepository;
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

    /**
     * This method retrieves a message containing a list of events grouped by day for a given date range.
     *
     * @param localDate the date for the user query
     * @param dayStart  the starting day of the query range. Minimum value: 1
     * @param dayFinish the ending day of the query range. Maximum value: length of the month for the given date
     * @return a message string with the list of events grouped by day
     */
    public String getMessageWithEventsGroupedByDay(final LocalDate localDate,
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

    /**
     * This method retrieves a message containing a list of events grouped by day for a given date range.
     *
     * @param localDate the date for the user query
     * @param dayStart  the starting day of the query range. Minimum value: 1
     * @param dayFinish the ending day of the query range. Maximum value: length of the month for the given date
     * @return a message string with the list of events grouped by day
     */
    public String getMessageWithEventsGroupedByDayFull(final LocalDate localDate,
                                                       final int dayStart,
                                                       final int dayFinish) {
        final Map<LocalDate, List<Event>> eventMap = getEventsAndGroupByDay(localDate, dayStart, dayFinish)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return formatMessageForEventsGroupedByDay(eventMap);
    }

    public int countEvents(final LocalDate localDate) {
        final int day = localDate.getDayOfMonth();
        final int month = localDate.getMonthValue();
        final int year = localDate.getYear();

        final LocalDateTime from = LocalDateTime.of(year, month, day, 0, 0);
        final LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59);
        return eventRepository.countEventsByStartDateBetween(from, end);
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

    private Map<LocalDate, List<Event>> getEventsAndGroupByDay(final LocalDate localDate,
                                                               final int dayStart,
                                                               final int dayFinish) {
        final LocalDateTime start = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), dayStart, 0, 0);
        final LocalDateTime end = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), dayFinish, 23, 59);
        return eventRepository.findEventsByStartDateBetween(start, end)
            .stream()
            .collect(Collectors.groupingBy(event -> event.getStartDate().toLocalDate()));
    }

}
