package com.balievent.telegrambot.bot.util;

import com.balievent.telegrambot.bot.constant.TelegramButton;
import com.balievent.telegrambot.entity.Event;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class KeyboardUtil {

    private static final int EVENT_LOCATIONS_SELECTION_COLS_COUNT = 2;

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

    public static InlineKeyboardMarkup getDetailedEventViewKeyboard(final Event event) {
        final List<InlineKeyboardButton> inlineKeyboardButtons2 = new ArrayList<>();
        inlineKeyboardButtons2.add(InlineKeyboardButton.builder()
            .url(event.getEventUrl())
            .text(TelegramButton.DETAILED_EVENT_VIEW_BUY_TICKET.getButtonText())
            .build());

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(inlineKeyboardButtons2))
            .build();
    }

    public static InlineKeyboardMarkup getShowMoreOptionsKeyboard(final int countEvent) {
        final List<InlineKeyboardButton> monthBackButtons = new ArrayList<>();
        monthBackButtons.add(InlineKeyboardButton.builder()
            .text(TelegramButton.SHOW_MORE_EVENT_LIST.getButtonText().formatted(countEvent))
            .callbackData(TelegramButton.SHOW_MORE_EVENT_LIST.getCallbackData())
            .build());

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(monthBackButtons))
            .build();
    }

    //    public static ReplyKeyboardMarkup getMainMenuKeyboard() {
    //        final List<KeyboardRow> keyboard = new ArrayList<>();
    //
    //        final KeyboardRow row = new KeyboardRow();
    //        row.add(TelegramButton.CHANGE_SEARCH_PARAMETERS.getButtonText());
    //        keyboard.add(row);
    //
    //        final KeyboardRow settingsAndHelp = new KeyboardRow();
    //        settingsAndHelp.add(TelegramButton.SETTINGS_AND_HELP.getButtonText());
    //        keyboard.add(settingsAndHelp);
    //
    //        final KeyboardRow contactAndFavorite = new KeyboardRow();
    //        contactAndFavorite.add(TelegramButton.CONTACT_US.getButtonText());
    //        keyboard.add(contactAndFavorite);
    //
    //        return ReplyKeyboardMarkup.builder()
    //            .keyboard(keyboard)
    //            .build();
    //    }

    public static InlineKeyboardMarkup createEventDateSelectionKeyboard() {

        final List<InlineKeyboardButton> firstRow = new ArrayList<>();

        firstRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.SEARCH_TODAY_EVENTS.getButtonText())
            .callbackData(TelegramButton.SEARCH_TODAY_EVENTS.getCallbackData())
            .build());

        firstRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.SEARCH_TOMORROW_EVENTS.getButtonText())
            .callbackData(TelegramButton.SEARCH_TOMORROW_EVENTS.getCallbackData())
            .build());

        final List<InlineKeyboardButton> secondRow = new ArrayList<>();
        secondRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.SEARCH_THIS_WEEK_EVENTS.getButtonText())
            .callbackData(TelegramButton.SEARCH_THIS_WEEK_EVENTS.getCallbackData())
            .build());
        secondRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.SEARCH_NEXT_WEEK_EVENTS.getButtonText())
            .callbackData(TelegramButton.SEARCH_NEXT_WEEK_EVENTS.getCallbackData())
            .build());

        final List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        thirdRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.SEARCH_ON_THIS_WEEKEND_EVENTS.getButtonText())
            .callbackData(TelegramButton.SEARCH_ON_THIS_WEEKEND_EVENTS.getCallbackData())
            .build());

        thirdRow.add(InlineKeyboardButton.builder()
            .text(TelegramButton.SEARCH_SHOW_ALL_EVENTS.getButtonText())
            .callbackData(TelegramButton.SEARCH_SHOW_ALL_EVENTS.getCallbackData())
            .build());

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(firstRow, secondRow, thirdRow))
            .build();
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public static InlineKeyboardMarkup createEventLocationsSelectionKeyboard(final List<String> allLocations,
                                                                             final List<String> selectedLocations) {
        final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (final String locationId : allLocations) {
            final String selectedIcon = selectedLocations.contains(locationId)
                ? "✅ "
                : "👉 ";

            row.add(InlineKeyboardButton.builder()
                .text(selectedIcon + locationId)
                .callbackData(locationId)
                .build());

            if (row.size() == EVENT_LOCATIONS_SELECTION_COLS_COUNT) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        // Добавляем кнопки "DESELECT_ALL"
        if (selectedLocations.contains(TelegramButton.DESELECT_ALL_LOCATIONS.getCallbackData())) {
            addNewButton(keyboard, TelegramButton.DESELECT_ALL_LOCATIONS.getButtonText(), TelegramButton.DESELECT_ALL_LOCATIONS.getCallbackData());
            // Добавляем кнопки "SELECT_ALL"
        } else if (selectedLocations.contains(TelegramButton.SELECT_ALL_LOCATIONS.getCallbackData())) {
            addNewButton(keyboard, TelegramButton.SELECT_ALL_LOCATIONS.getButtonText(), TelegramButton.SELECT_ALL_LOCATIONS.getCallbackData());
        }

        addNewButton(keyboard, TelegramButton.FILTER_EVENT_LOCATIONS_COMPLETE.getButtonText(), TelegramButton.FILTER_EVENT_LOCATIONS_COMPLETE.getCallbackData());

        return InlineKeyboardMarkup.builder()
            .keyboard(keyboard)
            .build();
    }

    private static void addNewButton(final List<List<InlineKeyboardButton>> keyboard,
                                     final String testString,
                                     final String callbackData) {
        final List<InlineKeyboardButton> nextButton = new ArrayList<>();
        nextButton.add(InlineKeyboardButton.builder()
            .text(testString)
            .callbackData(callbackData)
            .build());

        keyboard.add(nextButton);
    }

}

