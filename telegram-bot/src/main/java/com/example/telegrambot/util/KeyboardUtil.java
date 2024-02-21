package com.example.telegrambot.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@UtilityClass
public class KeyboardUtil {
    public static ReplyKeyboardMarkup getKeyboard(final int currentMonth, final int currentYear) {
        final int currentMonthNoZero = currentMonth == 0
                                       ? LocalDate.now().getMonthValue()
                                       : currentMonth;

        final Month month = Month.of(currentMonthNoZero);
        final Month monthNext = Month.of(currentMonthNoZero % 12 + 1);
        final Month monthPrevious = Month.of((currentMonthNoZero + 10) % 12 + 1);

        final int daysInMonth = month.length(LocalDate.now().isLeapYear());
        final String monthName = month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);

        final ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        final List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (int i = 1; i <= daysInMonth; i++) {
            final String buttonText = String.format("%02d %s", i, monthName);
            row.add(buttonText);

            if (i % 10 == 0) {
                keyboard.add(row);
                row = new KeyboardRow();
            }
        }

        keyboard.add(row);
        row = new KeyboardRow();

        final String buttonPreviousMonthText = String.format("%s (%02d.%04d)",
            monthPrevious,
            (currentMonthNoZero + 10) % 12 + 1,
            currentMonthNoZero == 1 ? currentYear - 1 : currentYear);

        row.add(buttonPreviousMonthText);

        final String buttonNextMonthText = String.format("%s (%02d.%04d)",
            monthNext,
            currentMonthNoZero % 12 + 1,
            currentMonthNoZero == 12 ? currentYear + 1 : currentYear);
        row.add(buttonNextMonthText);

        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
