package com.balievent.telegrambot.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class DateUtil {

    public static boolean isDateSelected(final String text) {
        final String datePatternWithDot = "\\d{2}\\.\\d{2}\\.\\d{4}";
        final String datePatternWithUnderscore = "/\\d{2}_\\d{2}_\\d{4}";
        return Pattern.matches(datePatternWithDot, text)
            || Pattern.matches(datePatternWithUnderscore, text);
    }

}
