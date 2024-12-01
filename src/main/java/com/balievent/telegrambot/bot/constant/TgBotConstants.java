package com.balievent.telegrambot.bot.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TgBotConstants {

    public static final String EVENT_NAME_TEMPLATE = """
        📅 %s""";

    public static final String EVENT_DATE_QUESTION = """
        When do you want to go to the event? 📅
        """;

    public static final String EVENT_LOCATIONS_QUESTION = "Great! Now choose the establishments where you'd like to spend your time.";

    public static final String GREETING_MESSAGE_TEMPLATE = """
        👋 Hello!
        I'm a bot that will help you find events in Bali. 🌴
        """;

    public static final String MORE_OPTIONS_TEMPLATE = """
        Here is the start ⬆️

        I have more options for you (%s) ⬇️
        """;

}
