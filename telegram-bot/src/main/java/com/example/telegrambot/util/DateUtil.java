package com.example.telegrambot.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class DateUtil {

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
    public static Integer getFullMonthNumber(final String text) {
        if (text.toUpperCase().contains("JANUARY (01.20")) {
            return 1;
        } else if (text.toUpperCase().contains("FEBRUARY (02.20")) {
            return 2;
        } else if (text.toUpperCase().contains("MARCH (03.20")) {
            return 3;
        } else if (text.toUpperCase().contains("APRIL (04.20")) {
            return 4;
        } else if (text.toUpperCase().contains("MAY (05.20")) {
            return 5;
        } else if (text.toUpperCase().contains("JUNE (06.20")) {
            return 6;
        } else if (text.toUpperCase().contains("JULY (07.20")) {
            return 7;
        } else if (text.toUpperCase().contains("AUGUST (08.20")) {
            return 8;
        } else if (text.toUpperCase().contains("SEPTEMBER (09.20")) {
            return 9;
        } else if (text.toUpperCase().contains("OCTOBER (10.20")) {
            return 10;
        } else if (text.toUpperCase().contains("NOVEMBER (11.20")) {
            return 11;
        } else if (text.toUpperCase().contains("DECEMBER (12.20")) {
            return 12;
        } else {
            return 0;
        }
    }

    /**
     * Проверка на корректность введенной даты dd.mm.YYYY.
     *
     * @param text - текст
     * @return boolean
     */
    public static boolean isCorrectDateFormat(final String text) {
        final String datePattern = "\\d{2}\\.\\d{2}\\.\\d{4}";
        return Pattern.matches(datePattern, text);
    }
}
