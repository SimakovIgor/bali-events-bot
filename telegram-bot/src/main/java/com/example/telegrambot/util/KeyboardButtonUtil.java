/**
 * Создал Андрей Антонов 2/19/2024 6:19 PM.
 **/

package com.example.telegrambot.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@UtilityClass
public class KeyboardButtonUtil {
    public static InlineKeyboardMarkup getFeedbackButton(String buttonText) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData("show_more");

        rowInline.add(inlineKeyboardButton);
        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public static ReplyKeyboardMarkup getCalendarKeyboard(Integer currentMonth, Integer currentYear) {
        if (currentMonth == null || currentMonth == 0) {
            currentMonth = LocalDate.now().getMonthValue(); // Получаем номер текущего месяца
        }
        Month month = Month.of(currentMonth); // название текущего месяца
        Month monthNext; // следующий месяц
        Month monthPrevious; // предыдущий месяц
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

        int daysInMonth = month.length(LocalDate.now().isLeapYear()); // Получаем количество дней в месяце
        String monthName = month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH); // Получаем две буквы названия месяца на английском а если нужно на вашем языке, то вместо Locale.ENGLISH -> java.util.Locale.getDefault()

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        String buttonText;

        int char1310 = 10; // сколько дней в одной строке
        if (daysInMonth == 31) {
            char1310 = char1310 + 1;
        }
        // Добавляем кнопки для дней в месяце
        for (int i = 1; i <= daysInMonth; i++) {
            buttonText = String.format("%02d %s", i, monthName); // Добавляем название месяца к номеру
            row.add(buttonText); // текст для кнопки

            if (row.size() % char1310 == 0) { // Добавляем не более 11 полей в строке
                keyboard.add(row);
                row = new KeyboardRow(); // ссылка на новую строку
            }
        }

        keyboard.add(row); // Добавляем строку для кнопок следующего и предыдущего месяца
        row = new KeyboardRow(); // ссылка на новую строку

        if (currentMonth == 12) {
            buttonText = String.format("%s (%02d.%04d)", monthPrevious, currentMonth - 1, currentYear); // Добавляем название предыдущего
            row.add(buttonText);

            buttonText = String.format("%s (%02d.%04d)", monthNext, 1, currentYear + 1); // Добавляем название следующего месяца
            row.add(buttonText);
        } else if (currentMonth == 1) {
            buttonText = String.format("%s (%02d.%04d)", monthPrevious, 12, currentYear - 1); // Добавляем название предыдущего
            row.add(buttonText);

            buttonText = String.format("%s (%02d.%04d)", monthNext, currentMonth + 1, currentYear); // Добавляем название следующего месяца
            row.add(buttonText);
        } else {
            buttonText = String.format("%s (%02d.%04d)", monthPrevious, currentMonth - 1, currentYear); // Добавляем название предыдущего
            row.add(buttonText);

            buttonText = String.format("%s (%02d.%04d)", monthNext, currentMonth + 1, currentYear); // Добавляем название следующего месяца
            row.add(buttonText);
        }

        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
