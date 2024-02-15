/**
 * Создал Андрей Антонов 2/13/2024 10:16 AM.
 **/

package com.example.telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {

    private final KeyboardUtil keyboardUtil;

    public MyTelegramBot(KeyboardUtil keyboardUtil) {

        super("6781420399:AAHi0vGFUPnh-7wBzC7si7hw1XRQmrNmPzA");       // указываем токен бота:

        this.keyboardUtil = keyboardUtil;
    }

    @Override
    public String getBotUsername() {
        return "BaliEventsCoordinatebot";                                      // Указывается имя бота
    }

    private final String LIST_OF_EVENTS_ON = "Список событий на: ";            // сообщение по умолчанию для списка событий
    private final String LIST_OF_POSSIBLE_QUESTIONS = "Список возможных вопросов: \n";
    private final String THIS_WORD_IS_NOT_RESERVED = "Это слово(а) не зарезервировано: ";
    private final String LIST_OF_RESERVED_WORDS_HELP = "\nСписок зарезервированных слов /help ";
    private final String WE_DO_NOT_HAVE_SUCH_DATA = "У нас нет таких данных: ";
    private final String INCORRECT_DATE = "Неверная дата";

    private Integer dayNumber = LocalDate.now().getDayOfMonth();            // текущее число (день месяца)
    private Integer monthNumber = LocalDate.now().getMonthValue();          // текущий месяца
    private Integer yearNumber = LocalDate.now().getYear();                 // текущий год

    @Override
    public void onUpdateReceived(Update update) {
        log.info("ПРИШЛО СООБЩЕНИЕ!!!  ");                                  // эти сообщения приходит в консоль
        log.info(update.getMessage().getFrom().getFirstName());
        log.info(update.getMessage().getText());

        String text = update.getMessage().getText();                        // получаем строку сообщения от пользователя
        SendMessage sendMessage = new SendMessage();                        // создаем класс для отправки сообщения
        sendMessage.setChatId(update.getMessage().getChatId());             // присваиваем идентификатор получателя сообщения

        AtomicInteger getDay = new AtomicInteger(0);               // сегодня число (день месяца) 0
        AtomicInteger getMonth = new AtomicInteger(0);             // текущий месяц по умолчанию 0
        AtomicInteger getYear = new AtomicInteger(0);              // текущий год по умолчанию 0

        parseDate(text, LIST_OF_EVENTS_ON, getDay, getMonth, getYear);      // получаем из строки день месяц и год

        if (text.contains("/start")) {
            this.dayNumber = LocalDate.now().getDayOfMonth();               // сегодня число (день месяца)
            this.monthNumber = LocalDate.now().getMonthValue();             // текущий месяц
            this.yearNumber = LocalDate.now().getYear();                    // текущий год
            sendMessageToDay(sendMessage);

        } else if ("/help".equals(text)) {
            sendMessage.setReplyMarkup(KeyboardUtil.getKeyboard(0, yearNumber));
            sendMessage.setText(LIST_OF_POSSIBLE_QUESTIONS);

        } else if (getMonthNumber(text) > 0) {                              // Наличие в строке месяца
            if (getDay.get() > 0) {
                this.dayNumber = getDay.get();                              // найденный ДЕНЬ присваиваем глобально
            }
            getMonthAndSendList(text, sendMessage);                         // Получаем месяц из строки

        } else if (getDay.get() > 0) {                                      // наличие в строке числа
            this.dayNumber = getDay.get();                                  // найденный ДЕНЬ присваиваем глобально
            if (getMonth.get() > 0) {
                this.monthNumber = getMonth.get();                          // найденный МЕСЯЦ присваиваем глобально
            }
            if (getYear.get() > 0) {
                this.yearNumber = getYear.get();                            // найденный ГОД присваиваем глобально
            }
            sendMessageToDay(sendMessage);
        } else {
            sendMessage.setText(THIS_WORD_IS_NOT_RESERVED + text + LIST_OF_RESERVED_WORDS_HELP);
        }

        try {
            execute(sendMessage);                                   // отправка сообщения пользователю
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void getMonthAndSendList(String text, SendMessage sendMessage) {
        int monthNumber = getMonthNumber(text);
        if (0 < monthNumber && monthNumber < 13) {
            if (this.monthNumber == 12 && monthNumber == 1) {       // переход на следующий год
                this.yearNumber++;
            }
            if (this.monthNumber == 1 && monthNumber == 12) {       // переход на предыдущий год
                this.yearNumber--;
            }
            this.monthNumber = monthNumber;

            sendMessageToDay(sendMessage);
        } else {
            sendMessage.setText(WE_DO_NOT_HAVE_SUCH_DATA + text);
        }
    }

    private Integer getInteger(String dayNumber) {
        int getDay;
        try {
            int day = Integer.parseInt(dayNumber);
            if (day > 0 && isValidDate(day, this.monthNumber, this.yearNumber)) {
                return day;
            } else {
                return 0;   // Если дата недействительна или день отрицательный, то возвращаем ноль
            }
        } catch (NumberFormatException e) {
            return 0;       // Если dayNumber не является числом, то возвращаем ноль
        }
    }

    private boolean isValidDate(int day, int month, int year) {
        try {
            LocalDate.of(year, month, day);
            return true;
        } catch (java.time.DateTimeException e) {
            day--;
            if (day > 0) {
                this.dayNumber = day;
                return isValidDate(day, month, year);
            } else {
                return false;
            }
        }
    }

    private void sendMessageToDay(SendMessage sendMessage) {
        if (isValidDate(this.dayNumber, this.monthNumber, this.yearNumber)) {
            // сообщение по умолчанию
            sendMessage.setText(String.format(Locale.getDefault(), LIST_OF_EVENTS_ON + "%02d.%02d.%dг.",
                this.dayNumber, this.monthNumber, this.yearNumber));
            // создаем календарь
            sendMessage.setReplyMarkup(KeyboardUtil.getKeyboard(this.monthNumber, this.yearNumber));
        } else {
            // Если дата недействительна, выполните необходимые действия
            // Например, отправьте пользователю сообщение об ошибке

            sendMessage.setText(INCORRECT_DATE);
        }
    }

    private Integer getMonthNumber(final String text) {
        if (text.toUpperCase().contains("JAN")) {
            return 1;
        } else if (text.toUpperCase().contains("FEB")) {
            return 2;
        } else if (text.toUpperCase().contains("MAR")) {
            return 3;
        } else if (text.toUpperCase().contains("APR")) {
            return 4;
        } else if (text.toUpperCase().contains("MAY")) {
            return 5;
        } else if (text.toUpperCase().contains("JUN")) {
            return 6;
        } else if (text.toUpperCase().contains("JUL")) {
            return 7;
        } else if (text.toUpperCase().contains("AUG")) {
            return 8;
        } else if (text.toUpperCase().contains("SEP")) {
            return 9;
        } else if (text.toUpperCase().contains("OCT")) {
            return 10;
        } else if (text.toUpperCase().contains("NOV")) {
            return 11;
        } else if (text.toUpperCase().contains("DEC")) {
            return 12;
        } else {
            return 0;
        }
    }

    public static void parseDate(final String text, final String constString, AtomicInteger getDay, AtomicInteger getMonth, AtomicInteger getYear) {
        final String newText;
        if (text.contains(constString)) {
            // Пример: "Список событий на: 01.01.2021г."
            newText = text.substring(constString.length(), constString.length() + 10);
        } else {
            newText = text;
        }

        if (newText.length() > 1) {
            // Пример: "14.02.2024" это ДЕНЬ
            getDay.set(getIntegerParseInt(newText.substring(0, 2).trim()));
            if (getDay.get() < 1 || getDay.get() > 31) {
                getDay.set(0);
            }
        }
        if (newText.length() > 4) {
            // Пример: "14.02.2024" это МЕСЯЦ
            getMonth.set(getIntegerParseInt(newText.substring(3, 5).trim()));
            if (getMonth.get() < 1 || getMonth.get() > 12) {
                getMonth.set(0);
            }
        }
        if (newText.length() > 9) {
            getYear.set(getIntegerParseInt(newText.substring(6, 10).trim())); // Пример: "14.02.2024" это ГОД
            if (getYear.get() < 100 && getYear.get() > 0) {
                getYear.set(2000 + getYear.get());
            } else if (getYear.get() < 1 || getYear.get() > 2100) {
                getYear.set(0);
            }
        } else if (newText.length() > 7) {
            getYear.set(getIntegerParseInt(newText.substring(6, 8).trim())); // Пример: "14.02.24" это ГОД
            if (getYear.get() < 100 && getYear.get() > 0) {
                getYear.set(2000 + getYear.get());
            } else if (getYear.get() < 1 || getYear.get() > 2100) {
                getYear.set(0);
            }
        }
    }

    public static Integer getIntegerParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
