package com.balievent.telegrambot.util;

import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.model.entity.Event;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@UtilityClass
public class MessageBuilderUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd_MM_yyyy");

    public static String buildEventsMessage(final Event event) {
        final StringBuilder result = new StringBuilder();

        final String line = event.getEventName() + "\n"
            + "Date: " + event.getStartDate().format(Settings.PRINT_DATE_TIME_FORMATTER) + "\n"
            + "Time: " + event.getStartDate().toLocalTime() + " - " + event.getEndDate().toLocalTime() + "\n";

        result.append(line);

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
