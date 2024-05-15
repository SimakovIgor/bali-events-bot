package com.balievent.telegrambot.util;

import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.model.dto.BriefDetailedLocationMessageDto;
import com.balievent.telegrambot.model.entity.Event;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@UtilityClass
public class MessageBuilderUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd_MM_yyyy");

    public static BriefDetailedLocationMessageDto buildBriefEventsMessage(final int currentPage,
                                                                          final List<Event> eventList) {
        // это цикл по всем событиям на текущий день.
        final Map<String, Long> locationMap = new HashMap<>();
        final StringBuilder result = new StringBuilder();

        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);

            final String line = "/"
                + (1 + i + Settings.PAGE_SIZE * (currentPage - 1))
                + "__"
                + processString(event.getEventName())
                + "\n";

            result.append(line);
            locationMap.put(line.trim(), event.getId());
        }

        return BriefDetailedLocationMessageDto.builder()
            .message(result.toString())
            .locationMap(locationMap)
            .build();
    }

    public static String processString(final String input) {
        // Удаляем все символы, кроме цифр, букв и пробелов
        String processed = input.replaceAll("[^\\p{Alnum} ]", "");
        // Заменяем пробелы на подчеркивания
        processed = processed.replace(" ", "_")
            .replace("__", "_");
        return processed;
    }

    public static String buildEventsMessage(final List<Event> eventList) {
        final StringBuilder result = new StringBuilder();

        for (final Event event : eventList) {
            final String line = event.getEventName() + "\n"
                + "Date: " + event.getStartDate().format(Settings.PRINT_DATE_TIME_FORMATTER) + "\n"
                + "Time: " + event.getStartDate().toLocalTime() + " - " + event.getEndDate().toLocalTime() + "\n"
                + CommonUtil.getLink("Buy Tickets Now!", event.getEventUrl()) + "\n"
                + GetGoogleMapLinkUtil.getGoogleMap("Location on Google map", event.getCoordinates()) + "\n";

            result.append(line);
        }
        return result.toString();
    }

    public static String formatMessageForEventsGroupedByDay(final Map<LocalDate, List<Event>> eventMap) {
        final Map<LocalDate, List<Event>> reverseSortedMap = new TreeMap<>(eventMap);

        final StringBuilder stringBuilder = new StringBuilder(30);
        reverseSortedMap.forEach((key, value) ->
            stringBuilder.append('/').append(key.format(DATE_TIME_FORMATTER))
                .append(" : ")
                .append(value.size())
                .append(" events\n"));

        if (stringBuilder.isEmpty()) {
            stringBuilder.append("No events");
        }

        return stringBuilder.toString();
    }
}
