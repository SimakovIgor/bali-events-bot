package com.example.telegrambot.util;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.service.MessageStorage;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;
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

    public static ReplyKeyboardMarkup setCalendar(final int currentMonth, final int currentYear) {
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

        int char1310 = 5; // количество дней в одной строке можно установить от 5 до 10
        if (daysInMonth == 31 && char1310 == 10) { // если в месяце 31 день то делаема 11 колонок в строке
            char1310 = char1310 + 1;
        }

        for (int i = 1; i <= daysInMonth; i++) {
            final String buttonText = String.format("%02d %s", i, monthName);
            row.add(buttonText);

            if (i % char1310 == 0 && i < 30) {
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

    public static InlineKeyboardMarkup setNewButton(final String buttonText, final String buttonName, final Update update, final MessageStorage messageStorage) {

        final List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        final List<InlineKeyboardButton> rowInline = new ArrayList<>();

        final InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);

        // Получаем номер сообщения для этого пользователя
        final int nextMessageNumber = messageStorage.getNextMessageNumber(update.getMessage().getChatId().toString());
        // Устанавливаем номер сообщения для этого пользователя
        inlineKeyboardButton.setCallbackData(buttonName + MyConstants.SHOW_SEPARATOR + nextMessageNumber);

        rowInline.add(inlineKeyboardButton);
        rowsInline.add(rowInline);

        final InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public InlineKeyboardMarkup setNewButton(final String buttonText, final String buttonName) {
        final List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        final List<InlineKeyboardButton> rowInline = new ArrayList<>();

        final InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(buttonName);

        rowInline.add(inlineKeyboardButton);
        rowsInline.add(rowInline);

        final InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public InlineKeyboardMarkup updateButton(final String callbackData) {
        // Получаем разметку кнопки с помощью нашего метода
        return setNewButton(MyConstants.SHOW_LESS_TEXT, callbackData);
    }

    public InlineKeyboardMarkup restoreButton(final String callbackData) {
        // Получаем разметку кнопки с помощью нашего метода
        return setNewButton(MyConstants.SHOW_MORE_TEXT, callbackData);
    }
}
