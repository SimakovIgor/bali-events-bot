/**
 * Создал Андрей Антонов 2/14/2024 12:57 PM.
 **/

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
    public static ReplyKeyboardMarkup getKeyboard(Integer currentMonth, Integer currentYear) {
        if (currentMonth == null || currentMonth == 0) {
            currentMonth = LocalDate.now().getMonthValue();
        }
        Month month = Month.of(currentMonth);
        Month monthNext;
        Month monthPrevious;
        if (currentMonth == 12) {
            monthNext = Month.of(1);
            monthPrevious = Month.of(currentMonth - 1);
        } else if (currentMonth == 1) {
            monthNext = Month.of(currentMonth + 1);
            monthPrevious = Month.of(12);
        } else {
            monthNext = Month.of(currentMonth + 1);
            monthPrevious = Month.of(currentMonth - 1);
        }

        int daysInMonth = month.length(LocalDate.now().isLeapYear());
        String monthName = month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        String buttonText;

        int char1310 = 10;
        if (daysInMonth == 31) {
            char1310 = char1310 + 1;
        }

        for (int i = 1; i <= daysInMonth; i++) {
            buttonText = String.format("%02d %s", i, monthName);
            row.add(buttonText);

            if (row.size() % char1310 == 0) {
                keyboard.add(row);
                row = new KeyboardRow();
            }
        }

        keyboard.add(row);
        row = new KeyboardRow();

        if (currentMonth == 12) {
            buttonText = String.format("%s (%02d.%04d)", monthPrevious, currentMonth - 1, currentYear);
            row.add(buttonText);

            buttonText = String.format("%s (%02d.%04d)", monthNext, 1, currentYear + 1);
            row.add(buttonText);
        } else if (currentMonth == 1) {
            buttonText = String.format("%s (%02d.%04d)", monthPrevious, 12, currentYear - 1);
            row.add(buttonText);

            buttonText = String.format("%s (%02d.%04d)", monthNext, currentMonth + 1, currentYear);
            row.add(buttonText);
        } else {
            buttonText = String.format("%s (%02d.%04d)", monthPrevious, currentMonth - 1, currentYear);
            row.add(buttonText);

            buttonText = String.format("%s (%02d.%04d)", monthNext, currentMonth + 1, currentYear);
            row.add(buttonText);
        }

        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
