package com.balievent.telegrambot.bot.util;

import com.balievent.telegrambot.bot.constant.Settings;
import com.balievent.telegrambot.model.entity.Event;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageBuilderUtil {

    public static String buildEventsMessage(final Event event) {
        final StringBuilder result = new StringBuilder();

        final String line = event.getEventName() + "\n\n"
            + "📅 Date: " + event.getStartDate().format(Settings.PRINT_DATE_TIME_FORMATTER) + " "
            + event.getStartDate().toLocalTime() + " - " + event.getEndDate().toLocalTime() + "\n"
            + "🏠 Place: " + event.getLocation().getId() + "\n\n"
            + "📍 Address: " + event.getLocation().getAddress() + "\n";

        result.append(line);

        return result.toString();
    }

}
