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
    public static ReplyKeyboardMarkup getKeyboard(int currentMonth, int currentYear) {
        if (currentMonth == 0) {
            currentMonth = LocalDate.now().getMonthValue();
        }

        Month month = Month.of(currentMonth);
        Month monthNext = Month.of(currentMonth % 12 + 1);
        Month monthPrevious = Month.of((currentMonth + 10) % 12 + 1);

        int daysInMonth = month.length(LocalDate.now().isLeapYear());
        String monthName = month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        String buttonText;

        for (int i = 1; i <= daysInMonth; i++) {
            buttonText = String.format("%02d %s", i, monthName);
            row.add(buttonText);

            if (i % 10 == 0) {
                keyboard.add(row);
                row = new KeyboardRow();
            }
        }

        keyboard.add(row);
        row = new KeyboardRow();

        buttonText = String.format("%s (%02d.%04d)",
            monthPrevious,
            (currentMonth + 10) % 12 + 1,
            currentMonth == 1 ? currentYear - 1 : currentYear);

        row.add(buttonText);

        buttonText = String.format("%s (%02d.%04d)",
            monthNext,
            currentMonth % 12 + 1,
            currentMonth == 12 ? currentYear + 1 : currentYear);
        row.add(buttonText);

        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
