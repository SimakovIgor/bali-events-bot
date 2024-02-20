package com.example.telegrambot.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class DateUtil {

    /**
     * Проверка на корректность введенной даты dd.mm.YYYY
     *
     * @param text - текст
     * @return boolean
     */
    public static boolean isCorrectDateFormat(String text) {
        String datePattern = "\\d{2}\\.\\d{2}\\.\\d{4}";
        return Pattern.matches(datePattern, text);
    }
}
