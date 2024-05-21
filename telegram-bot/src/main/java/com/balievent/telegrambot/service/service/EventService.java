package com.balievent.telegrambot.service.service;

import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.repository.EventRepository;
import com.balievent.telegrambot.util.MessageBuilderUtil;
import lombok.RequiredArgsConstructor;
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

    public String getMessageWithEventsGroupedByDay(final LocalDate localDate,
                                                   final int dayStart,
                                                   final int dayFinish) {
        final Map<LocalDate, List<Event>> eventMap = getEventsAndGroupByDay(localDate, dayStart, dayFinish)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return MessageBuilderUtil.formatMessageForEventsGroupedByDay(eventMap);
    }

    public Map<LocalDate, List<Event>> getEventsAndGroupByDay(final LocalDate localDate,
                                                              final int dayStart,
                                                              final int dayFinish) {
        final LocalDateTime start = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), dayStart, 0, 0);
        final LocalDateTime end = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), dayFinish, 23, 59);
        return eventRepository.findEventsByStartDateBetween(start, end)
            .stream()
            .collect(Collectors.groupingBy(event -> event.getStartDate().toLocalDate()));
    }

    public Map<LocalDate, List<Event>> getEventsAndGroupByDay(final LocalDateTime start,
                                                              final LocalDateTime end) {
        return eventRepository.findEventsByStartDateBetween(start, end)
            .stream()
            .collect(Collectors.groupingBy(event -> event.getStartDate().toLocalDate()));
    }

}
