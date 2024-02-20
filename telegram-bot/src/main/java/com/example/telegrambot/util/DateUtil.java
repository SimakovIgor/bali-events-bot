package com.example.telegrambot.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@UtilityClass
public class DateUtil {
    public static void parseDate(final String text,
                                 final String constString,
                                 final AtomicInteger getDay,
                                 final AtomicInteger getMonth,
                                 final AtomicInteger getYear) {
        final String newText;
        if (text.contains(constString)) {
            // Пример: "Список событий на: 01.01.2021г."
            newText = text.substring(constString.length(), constString.length() + 10);
        } else {
            newText = text;
        }

        if (newText.length() > 1) {
            // Пример: "14.02.2024" это ДЕНЬ
            getDay.set(getIntegerParseInt(newText.substring(0, 2).trim()));
            if (getDay.get() < 1 || getDay.get() > 31) {
                getDay.set(0);
            }
        }
        if (newText.length() > 4) {
            // Пример: "14.02.2024" это МЕСЯЦ
            getMonth.set(getIntegerParseInt(newText.substring(3, 5).trim()));
            if (getMonth.get() < 1 || getMonth.get() > 12) {
                getMonth.set(0);
            }
        }
        if (newText.length() > 9) {
            getYear.set(getIntegerParseInt(newText.substring(6, 10).trim())); // Пример: "14.02.2024" это ГОД
            if (getYear.get() < 100 && getYear.get() > 0) {
                getYear.set(2000 + getYear.get());
            } else if (getYear.get() < 1 || getYear.get() > 2100) {
                getYear.set(0);
            }
        } else if (newText.length() > 7) {
            getYear.set(getIntegerParseInt(newText.substring(6, 8).trim())); // Пример: "14.02.24" это ГОД
            if (getYear.get() < 100 && getYear.get() > 0) {
                getYear.set(2000 + getYear.get());
            } else if (getYear.get() < 1 || getYear.get() > 2100) {
                getYear.set(0);
            }
        }
    }

    public static Integer getIntegerParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Integer getMonthNumber(final String text) {
        if (text.toUpperCase().contains("JAN")) {
            return 1;
        } else if (text.toUpperCase().contains("FEB")) {
            return 2;
        } else if (text.toUpperCase().contains("MAR")) {
            return 3;
        } else if (text.toUpperCase().contains("APR")) {
            return 4;
        } else if (text.toUpperCase().contains("MAY")) {
            return 5;
        } else if (text.toUpperCase().contains("JUN")) {
            return 6;
        } else if (text.toUpperCase().contains("JUL")) {
            return 7;
        } else if (text.toUpperCase().contains("AUG")) {
            return 8;
        } else if (text.toUpperCase().contains("SEP")) {
            return 9;
        } else if (text.toUpperCase().contains("OCT")) {
            return 10;
        } else if (text.toUpperCase().contains("NOV")) {
            return 11;
        } else if (text.toUpperCase().contains("DEC")) {
            return 12;
        } else {
            return 0;
        }
    }

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
