package com.balievent.telegrambot.util;

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
            .build();
    }

    public InlineKeyboardMarkup setShowMoreButtonKeyboard(final String buttonText,
                                                          final String callbackData) {
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

    /**
     * Получаем разметку клавиатуры для пагинации (переключения страниц)
     * Это две кнопки влево и вправо
     *
     * @return - разметка клавиатуры
     */
    public static InlineKeyboardMarkup getPaginationKeyboard(final int currentPage, final int pageCount) {
        if (pageCount == 0) {
            return InlineKeyboardMarkup.builder().build();
        }

        final List<InlineKeyboardButton> row = new ArrayList<>();

        if (currentPage > 2) {
            row.add(InlineKeyboardButton.builder()
                .text("<< 1")
                .callbackData("first_page")
                .build());
        }

        if (currentPage > 1) {
            row.add(InlineKeyboardButton.builder()
                .text("< " + (currentPage - 1))
                .callbackData("previous_page")
                .build());
        }

        row.add(InlineKeyboardButton.builder()
            .text(currentPage + " / " + pageCount)
            .callbackData("current_page")
            .build());

        if (currentPage < pageCount) {
            row.add(InlineKeyboardButton.builder()
                .text("> " + (currentPage + 1))
                .callbackData("next_page")
                .build());
        }

        if (currentPage < pageCount - 1) {
            row.add(InlineKeyboardButton.builder()
                .text(">> " + pageCount)
                .callbackData("last_page")
                .build());
        }

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(row))
            .build();
    }

}
