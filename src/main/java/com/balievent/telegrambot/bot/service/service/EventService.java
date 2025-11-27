package com.balievent.telegrambot.bot.service.service;

import com.balievent.telegrambot.bot.constant.TelegramButton;
import com.balievent.telegrambot.entity.Event;
import com.balievent.telegrambot.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    public static final LocalTime START_DAY_LOCAL_TIME = LocalTime.of(0, 0);
    public static final LocalTime END_DAY_LOCAL_TIME = LocalTime.of(23, 59, 59);

    private final EventRepository eventRepository;

    public List<Event> getEventsAndGroupByDay(final LocalDateTime start,
                                              final LocalDateTime end) {
        return eventRepository.findEventsByStartDateTimeBetween(start, end)
            .stream()
            .toList();
    }

    public List<Event> getFilteredEventList(final TelegramButton telegramButton) {
        final LocalDate now = LocalDate.now();

        return switch (telegramButton) {
            case SEARCH_TODAY_EVENTS -> {
                final var start = LocalDateTime.of(now, START_DAY_LOCAL_TIME);
                final var end = LocalDateTime.of(now, END_DAY_LOCAL_TIME);

                yield getEventsAndGroupByDay(start, end);
            }
            case SEARCH_TOMORROW_EVENTS -> {
                final LocalDate tomorrow = now.plusDays(1);

                final var start = LocalDateTime.of(tomorrow, START_DAY_LOCAL_TIME);
                final var end = LocalDateTime.of(tomorrow, END_DAY_LOCAL_TIME);

                yield getEventsAndGroupByDay(start, end);
            }
            case SEARCH_THIS_WEEK_EVENTS -> {
                final LocalDate endLocalDate = now.plusWeeks(1);

                final var start = LocalDateTime.of(now, START_DAY_LOCAL_TIME);
                final var end = LocalDateTime.of(endLocalDate, END_DAY_LOCAL_TIME);

                yield getEventsAndGroupByDay(start, end);
            }
            case SEARCH_NEXT_WEEK_EVENTS -> {
                final LocalDate startLocalDate = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                final LocalDate endLocalDate = startLocalDate.plusWeeks(1);

                final var start = LocalDateTime.of(startLocalDate, START_DAY_LOCAL_TIME);
                final var end = LocalDateTime.of(endLocalDate, END_DAY_LOCAL_TIME);

                yield getEventsAndGroupByDay(start, end);
            }
            case SEARCH_ON_THIS_WEEKEND_EVENTS -> {
                final LocalDate startLocalDate = now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
                final LocalDate endLocalDate = now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

                final var start = LocalDateTime.of(startLocalDate, START_DAY_LOCAL_TIME);
                final var end = LocalDateTime.of(endLocalDate, END_DAY_LOCAL_TIME);

                yield getEventsAndGroupByDay(start, end);
            }
            case SEARCH_SHOW_ALL_EVENTS -> {
                final var start = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0);
                final var end = LocalDateTime.of(now.getYear(), now.getMonth(), now.lengthOfMonth(), 0, 0);

                yield getEventsAndGroupByDay(start, end);
            }

            default -> throw new IllegalStateException("Unexpected value: " + telegramButton);
        };

    }
}
