package com.balievent.telegrambot.util;

import com.balievent.telegrambot.contant.MyConstants;
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
public class KeyboardUtil {

    public static final int COLS_COUNT = 5; // количество дней в одной строке можно установить от 5 до 10

    public static ReplyKeyboardMarkup setCalendar(final int currentMonth, final int currentYear) {
        final int currentMonthNoZero = currentMonth == 0
                                       ? LocalDate.now().getMonthValue()
                                       : currentMonth;

        final Month month = Month.of(currentMonthNoZero);
        final Month monthNext = Month.of(currentMonthNoZero % 12 + 1);
        final Month monthPrevious = Month.of((currentMonthNoZero + 10) % 12 + 1);

        final int daysInMonth = month.length(LocalDate.now().isLeapYear());
        final String monthName = month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);

        final List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (int i = 1; i <= daysInMonth; i++) {
            final String buttonText = String.format("%02d %s", i, monthName);
            row.add(buttonText);

            if (i % COLS_COUNT == 0 && i < 30) {
                keyboard.add(row);
                row = new KeyboardRow();
            }
        }

        keyboard.add(row);

        final KeyboardRow monthChangeRow = new KeyboardRow();
        final String buttonPreviousMonthText = String.format("%s (%02d.%04d)",
            monthPrevious,
            (currentMonthNoZero + 10) % 12 + 1,
            currentMonthNoZero == 1 ? currentYear - 1 : currentYear);
        monthChangeRow.add(buttonPreviousMonthText);

        final String buttonNextMonthText = String.format("%s (%02d.%04d)",
            monthNext,
            currentMonthNoZero % 12 + 1,
            currentMonthNoZero == 12 ? currentYear + 1 : currentYear);
        monthChangeRow.add(buttonNextMonthText);
        keyboard.add(monthChangeRow);

        return ReplyKeyboardMarkup.builder()
            .keyboard(keyboard)
            .isPersistent(true)
            .build();
    }

    public static InlineKeyboardMarkup setShowMoreButtonKeyboard(final Long nextMessageNumber,
                                                                 final String callbackName) {
        final InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
            .text(MyConstants.SHOW_MORE_TEXT)
            // Устанавливаем номер сообщения для этого пользователя
            .callbackData(callbackName + MyConstants.COLON_MARK + nextMessageNumber)
            .build();

        final List<InlineKeyboardButton> rowInline = List.of(inlineKeyboardButton);
        final List<List<InlineKeyboardButton>> rowsInline = List.of(rowInline);

        return InlineKeyboardMarkup.builder()
            .keyboard(rowsInline)
            .build();
    }

    public InlineKeyboardMarkup setShowMoreButtonKeyboard(final String buttonText, final String callbackData) {
        final InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
            .text(buttonText)
            .callbackData(callbackData)
            .build();

        final List<InlineKeyboardButton> rowInline = List.of(inlineKeyboardButton);
        final List<List<InlineKeyboardButton>> rowsInline = List.of(rowInline);

        return InlineKeyboardMarkup.builder()
            .keyboard(rowsInline)
            .build();
    }

    public static InlineKeyboardMarkup getPaginationKeyboard() {
        final List<InlineKeyboardButton> row = List.of(
            InlineKeyboardButton.builder()
                .text("⬅️ Previous")
                .callbackData("previous_pagination")
                .build(),
            InlineKeyboardButton.builder()
                .text("Next ➡️")
                .callbackData("next_pagination")
                .build()
        );

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(row))
            .build();
    }

    public InlineKeyboardMarkup updateButton(final String callbackData) {
        // Получаем разметку кнопки с помощью нашего метода
        return setShowMoreButtonKeyboard(MyConstants.SHOW_LESS_TEXT, callbackData);
    }

    public InlineKeyboardMarkup restoreButton(final String callbackData) {
        return setShowMoreButtonKeyboard(MyConstants.SHOW_MORE_TEXT, callbackData);
    }
}
