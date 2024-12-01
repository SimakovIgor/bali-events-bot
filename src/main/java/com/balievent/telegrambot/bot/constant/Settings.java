package com.balievent.telegrambot.bot.constant;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Settings {
    public static final int SHOW_EVENTS_COUNT = 20;
    public static final DateTimeFormatter PRINT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
}
