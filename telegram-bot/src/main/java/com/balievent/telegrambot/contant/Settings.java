package com.balievent.telegrambot.contant;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Settings {
    public static final int PAGE_SIZE = 8;
    public static final DateTimeFormatter PRINT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
}
