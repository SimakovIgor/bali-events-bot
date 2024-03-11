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

    private static final int COLS_COUNT = 5;
    private static final int EVENT_LOCATIONS_SELECTION_COLS_COUNT = 2;

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

    public static ReplyKeyboardMarkup getMainMenuKeyboard() {
        final List<KeyboardRow> keyboard = new ArrayList<>();

        final KeyboardRow row = new KeyboardRow();
        row.add(TelegramButton.CHANGE_SEARCH_PARAMETERS.getButtonText());
        keyboard.add(row);

        final KeyboardRow settingsAndHelp = new KeyboardRow();
        settingsAndHelp.add(TelegramButton.SETTINGS_AND_HELP.getButtonText());
        keyboard.add(settingsAndHelp);

        final KeyboardRow contactAndFavorite = new KeyboardRow();
        contactAndFavorite.add(TelegramButton.CONTACT_US.getButtonText());
        contactAndFavorite.add(TelegramButton.FAVORITE_EVENTS.getButtonText());
        keyboard.add(contactAndFavorite);

        return ReplyKeyboardMarkup.builder()
            .keyboard(keyboard)
            .build();
    }

    public static InlineKeyboardMarkup createEventDateSelectionKeyboard() {
        final List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.TODAY_EVENTS.getButtonText())
            .callbackData(TelegramButton.TODAY_EVENTS.getCallbackData())
            .build());
        firstRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.TOMORROW_EVENTS.getButtonText())
            .callbackData(TelegramButton.TOMORROW_EVENTS.getCallbackData())
            .build());

        final List<InlineKeyboardButton> secondRow = new ArrayList<>();
        secondRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.THIS_WEEK_EVENTS.getButtonText())
            .callbackData(TelegramButton.THIS_WEEK_EVENTS.getCallbackData())
            .build());
        secondRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.NEXT_WEEK_EVENTS.getButtonText())
            .callbackData(TelegramButton.NEXT_WEEK_EVENTS.getCallbackData())
            .build());

        final List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        thirdRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.ON_THIS_WEEKEND_EVENTS.getButtonText())
            .callbackData(TelegramButton.ON_THIS_WEEKEND_EVENTS.getCallbackData())
            .build());
        thirdRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.SHOW_ALL_EVENTS.getButtonText())
            .callbackData(TelegramButton.SHOW_ALL_EVENTS.getCallbackData())
            .build());

        final List<InlineKeyboardButton> fourthRow = new ArrayList<>();
        fourthRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.PICK_DATE_EVENTS.getButtonText())
            .callbackData(TelegramButton.PICK_DATE_EVENTS.getCallbackData())
            .build());

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(firstRow, secondRow, thirdRow, fourthRow))
            .build();

    }

    public static InlineKeyboardMarkup createEventLocationsSelectionKeyboard(final List<String> locations) {
        final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        final List<InlineKeyboardButton> row = new ArrayList<>();

        for (String location : locations) {
            row.add(InlineKeyboardButton.builder()
                .text("👉" + location)
                .callbackData(location)
                .build());

            if (row.size() == EVENT_LOCATIONS_SELECTION_COLS_COUNT) {
                keyboard.add(row);
                row.clear();
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        return InlineKeyboardMarkup.builder()
            .keyboard(keyboard)
            .build();
    }

}
