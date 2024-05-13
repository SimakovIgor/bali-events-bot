package com.balievent.telegrambot.service.service;

import com.balievent.telegrambot.exceptions.ErrorCode;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.repository.EventRepository;
import com.balievent.telegrambot.util.MessageBuilderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

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
        final Map<LocalDate, List<Event>> eventMap = getEventsAndGroupByDay(localDate, dayStart, dayFinish)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return MessageBuilderUtil.formatMessageForEventsGroupedByDay(eventMap);
    }

    public String getMessageWithEventsGroupedByDay(final LocalDateTime start,
                                                   final LocalDateTime end) {
        final Map<LocalDate, List<Event>> eventMap = getEventsAndGroupByDay(start, end)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return MessageBuilderUtil.formatMessageForEventsGroupedByDay(eventMap);
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

    private Map<LocalDate, List<Event>> getEventsAndGroupByDay(final LocalDateTime start,
                                                               final LocalDateTime end) {
        return eventRepository.findEventsByStartDateBetween(start, end)
            .stream()
            .collect(Collectors.groupingBy(event -> event.getStartDate().toLocalDate()));
    }

    //todo refactor
    public List<Event> findEventsById(final Long id) {
        final Event event = eventRepository.findById(id)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_999));
        return List.of(event);
    }
}
