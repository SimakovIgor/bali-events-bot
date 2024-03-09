package com.balievent.telegrambot.util;

import com.balievent.telegrambot.constant.TelegramButton;
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

    public static final int COLS_COUNT = 5;

    public static ReplyKeyboardMarkup setCalendar(final int currentMonth) {
        final Month month = Month.of(currentMonth);

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

        final String buttonPreviousMonthText = getPreviousMonthButtonText(currentMonth);
        final String buttonNextMonthText = getNextMonthButtonText(currentMonth);

        final KeyboardRow monthChangeRow = new KeyboardRow();
        monthChangeRow.add(buttonPreviousMonthText);
        monthChangeRow.add(buttonNextMonthText);

        keyboard.add(monthChangeRow);

        return ReplyKeyboardMarkup.builder()
            .keyboard(keyboard)
            .build();
    }

    public InlineKeyboardMarkup createInlineKeyboard(final TelegramButton telegramButton) {
        final InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
            .text(telegramButton.getButtonText())
            .callbackData(telegramButton.getCallbackData())
            .build();

        final List<InlineKeyboardButton> rowInline = List.of(inlineKeyboardButton);
        final List<List<InlineKeyboardButton>> rowsInline = List.of(rowInline);

        return InlineKeyboardMarkup.builder()
            .keyboard(rowsInline)
            .build();
    }

    public InlineKeyboardMarkup createMonthInlineKeyboard(final LocalDate calendarDate) {
        final int monthValue = calendarDate.getMonthValue();

        final InlineKeyboardButton previousMonthButton = InlineKeyboardButton.builder()
            .text(getPreviousMonthButtonText(monthValue))
            .callbackData(TelegramButton.PREVIOUS_MONTH_PAGE.getCallbackData())
            .build();
        final InlineKeyboardButton nextMonthButton = InlineKeyboardButton.builder()
            .text(getNextMonthButtonText(monthValue))
            .callbackData(TelegramButton.NEXT_MONTH_PAGE.getCallbackData())
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(previousMonthButton, nextMonthButton)
            ))
            .build();
    }

    private static String getNextMonthButtonText(final int currentMonth) {
        final Month monthNext = Month.of(currentMonth % 12 + 1);
        return String.format(TelegramButton.NEXT_MONTH_PAGE.getButtonText().formatted(monthNext));
    }

    private static String getPreviousMonthButtonText(final int currentMonth) {
        final Month monthPrevious = Month.of((currentMonth + 10) % 12 + 1);
        return String.format(TelegramButton.PREVIOUS_MONTH_PAGE.getButtonText().formatted(monthPrevious));
    }

    /**
     * Generates an inline keyboard markup for navigating between pages of day events.
     * Can include buttons for the first, previous, next, and last pages (if pageCount > 0)
     * Also includes a button to return to the month view
     *
     * @param currentPage The current page number.
     * @param pageCount   The total number of pages.
     * @return The generated InlineKeyboardMarkup.
     */
    public static InlineKeyboardMarkup getDayEventsKeyboard(final int currentPage, final int pageCount) {
        final List<InlineKeyboardButton> monthBackButtons = new ArrayList<>();
        monthBackButtons.add(InlineKeyboardButton.builder()
            .text(TelegramButton.MONTH_EVENTS_PAGE.getButtonText())
            .callbackData(TelegramButton.MONTH_EVENTS_PAGE.getCallbackData())
            .build());

        if (pageCount == 0) {
            return InlineKeyboardMarkup.builder()
                .keyboard(List.of(monthBackButtons))
                .build();
        }

        final List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
        if (currentPage > 2) {
            final TelegramButton firstEventsPageButton = TelegramButton.FIRST_EVENTS_PAGE;
            paginationButtons.add(InlineKeyboardButton.builder()
                .text(firstEventsPageButton.getButtonText().formatted(pageCount))
                .callbackData(firstEventsPageButton.getCallbackData())
                .build());
        }

        if (currentPage > 1) {
            final TelegramButton previousEventsPageButton = TelegramButton.PREVIOUS_EVENTS_PAGE;
            paginationButtons.add(InlineKeyboardButton.builder()
                .text(previousEventsPageButton.getButtonText().formatted(currentPage - 1, pageCount))
                .callbackData(previousEventsPageButton.getCallbackData())
                .build());
        }

        if (currentPage < pageCount) {
            final TelegramButton nextEventsPageButton = TelegramButton.NEXT_EVENTS_PAGE;
            paginationButtons.add(InlineKeyboardButton.builder()
                .text(nextEventsPageButton.getButtonText().formatted(currentPage + 1, pageCount))
                .callbackData(nextEventsPageButton.getCallbackData())
                .build());
        }

        if (currentPage < pageCount - 1) {
            final TelegramButton lastEventsPageButton = TelegramButton.LAST_EVENTS_PAGE;
            paginationButtons.add(InlineKeyboardButton.builder()
                .text(lastEventsPageButton.getButtonText().formatted(pageCount, pageCount))
                .callbackData(lastEventsPageButton.getCallbackData())
                .build());
        }

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(paginationButtons, monthBackButtons))
            .build();
    }

}
