/**
 * Создал Андрей Антонов 2/13/2024 10:16 AM.
 **/

package com.example.telegrambot.service;

import com.example.telegrambot.contant.Constants;
import com.example.telegrambot.util.DateUtil;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {

    private final CalendarProcessService calendarProcessService;

    public MyTelegramBot(final CalendarProcessService calendarProcessService) {
        super("6781420399:AAHi0vGFUPnh-7wBzC7si7hw1XRQmrNmPzA");       // указываем токен бота:
        this.calendarProcessService = calendarProcessService;
    }

    private static boolean isCalendarEvent(String text, AtomicInteger getDay) {
        return DateUtil.getMonthNumber(text) > 0 || getDay.get() > 0;
    }

    @Override
    public String getBotUsername() {
        return "BaliEventsCoordinatebot";                                      // Указывается имя бота
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            log.info("ПРИШЛО СООБЩЕНИЕ!!!  ");                                  // эти сообщения приходит в консоль
            log.info(update.getMessage().getFrom().getFirstName());
            log.info(update.getMessage().getText());

            String text = update.getMessage().getText();                        // получаем строку сообщения от пользователя
            SendMessage sendMessage = new SendMessage();                        // создаем класс для отправки сообщения
            sendMessage.setChatId(update.getMessage().getChatId());             // присваиваем идентификатор получателя сообщения

            AtomicInteger getDay = new AtomicInteger(0);               // сегодня число (день месяца) 0
            AtomicInteger getMonth = new AtomicInteger(0);             // текущий месяц по умолчанию 0
            AtomicInteger getYear = new AtomicInteger(0);              // текущий год по умолчанию 0

            DateUtil.parseDate(text, Constants.LIST_OF_EVENTS_ON, getDay, getMonth, getYear);      // получаем из строки день месяц и год

            if (text.contains("/start")) {
                calendarProcessService.setDay(LocalDate.now().getDayOfMonth());               // сегодня число (день месяца)
                calendarProcessService.setMonth(LocalDate.now().getMonthValue());             // текущий месяц
                calendarProcessService.setYear(LocalDate.now().getYear());

                sendStartMessage(sendMessage, calendarProcessService.getDay(), calendarProcessService.getMonth(), calendarProcessService.getYear());

            } else if (isCalendarEvent(text, getDay)) {                    //Обработка календарных дат
                sendMessage = calendarProcessService.process(update, getDay, getMonth, getYear);
            } else {
                sendMessage.setText(Constants.THIS_WORD_IS_NOT_RESERVED + text + Constants.LIST_OF_RESERVED_WORDS_HELP);
            }

            execute(sendMessage);                                   // отправка сообщения пользователю

        } catch (TelegramApiException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void sendStartMessage(SendMessage sendMessage, Integer day, Integer month, Integer year) {
        sendMessage.setText("Привет! Я бот, который поможет тебе найти события на Бали. Напиши дату в формате: 'dd.mm.yyyy'");
        sendMessage.setReplyMarkup(KeyboardUtil.getKeyboard(month, year));
    }

}
