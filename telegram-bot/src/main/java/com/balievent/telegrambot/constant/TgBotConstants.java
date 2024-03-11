package com.balievent.telegrambot.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TgBotConstants {

    public static final String EVENT_LIST_TEMPLATE = """
        📅 List of events on: %s

        %s""";
    public static final String EVENT_DATE_QUESTION = """
        When do you want to go to the event? 📅
        """;

    public static final String EVENT_TYPE_QUESTION = """
        Great! Now choose the establishments where you'd like to spend your time. 🏢
        You can select multiple options.
        """;

    public static final String GREETING_MESSAGE_TEMPLATE = """
        👋 Hello!
        I'm a bot that will help you find events in Bali. 🌴
        Write the date in the format: 'dd.mm.yyyy' or choose from the calendar
        """;
}
