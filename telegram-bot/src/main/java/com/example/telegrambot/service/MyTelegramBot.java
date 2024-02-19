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
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {


    private final CalendarProcessService calendarProcessService;
    AtomicInteger startMessageId = new AtomicInteger(0);        // идентификатор последнего сообщения

    public MyTelegramBot(final CalendarProcessService calendarProcessService) {
        super("6781420399:AAHi0vGFUPnh-7wBzC7si7hw1XRQmrNmPzA");       // указываем токен бота:

        this.calendarProcessService = calendarProcessService;
    }

    @Override
    public String getBotUsername() {
        return "BaliEventsCoordinatebot";                                      // Указывается имя бота
    }
    private static boolean isCalendarEvent(String text, AtomicInteger getDay) {
        return DateUtil.getMonthNumber(text) > 0 || getDay.get() > 0;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) { // Обработка нажатия кнопки "Show more"
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Long chatId = callbackQuery.getMessage().getChatId();
            String callbackData = callbackQuery.getData();
            callbackQuery.getInlineMessageId();

            if (callbackData.equals("show_more")) {
                // Загрузка дополнительной информации с сервера
                String additionalInfo = loadAdditionalInfo();

                // Обновление сообщения с дополнительной информацией
                SendMessage additionalMessage = new SendMessage();
                additionalMessage.setChatId(chatId);
                additionalMessage.setReplyToMessageId(startMessageId.get());

                additionalMessage.setText(callbackData  + "\n\nДополнительная информация: " + additionalInfo);
                try {
                    execute(additionalMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

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
            List<InputMediaPhoto> media = new ArrayList<>();                    // список ссылок на картинки

            DateUtil.parseDate(text, Constants.LIST_OF_EVENTS_ON, getDay, getMonth, getYear);  // получаем из строки день месяц и год

            if (text.contains("/start")) {
                calendarProcessService.setDay(LocalDate.now().getDayOfMonth());  // сегодня число (день месяца)
                calendarProcessService.setMonth(LocalDate.now().getMonthValue());// текущий месяц
                calendarProcessService.setYear(LocalDate.now().getYear());

                sendStartMessage(sendMessage, calendarProcessService.getDay(), calendarProcessService.getMonth(), calendarProcessService.getYear());

            } else if (isCalendarEvent(text, getDay)) {                          //Обработка календарных дат
                sendMessage = calendarProcessService.process(update, getDay, getMonth, getYear, media); // создаем текстовое сообщение
                sendMediaGroup(update, media, sendMessage);                      // создаем группу картинок
            } else {
                sendMessage.setText(Constants.THIS_WORD_IS_NOT_RESERVED + text + Constants.LIST_OF_RESERVED_WORDS_HELP);
            }
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.disableWebPagePreview();
            sendMessage.enableHtml(true);

            Message sentMessage = execute(sendMessage);                          // отправка сообщения пользователю
            Integer messageId = sentMessage.getMessageId();                      // Сохраняем идентификатор сообщения
            startMessageId.set(messageId);
        } catch (TelegramApiException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void sendMediaGroup(Update update, List<InputMediaPhoto> media, SendMessage sendMessage) throws TelegramApiException {
        SendMediaGroup sendMediaGroupRequest = new SendMediaGroup();             // Создаем объект группа картинок
        sendMediaGroupRequest.setChatId(update.getMessage().getChatId().toString());
        List<InputMedia> inputMediaList = new ArrayList<>(media);
        if (inputMediaList.size() > 0) {
            sendMediaGroupRequest.setMedias(inputMediaList);
            execute(sendMediaGroupRequest);                                      // отправка картинок пользователю

//            sendMessage.setReplyMarkup(InlineKeyboardMarkupUtil
//                .createInlineKeyboardMarkup("Show more info"));         // кнопка 'Show more info' для текстового сообщения
        } else {
            sendMessage.setText(sendMessage.getText() + Constants.THERE_ARE_NO_RECORDS_IN_THE_DATABASE_FOR_THIS_DATE);
        }
    }

    private void sendStartMessage(SendMessage sendMessage, Integer day, Integer month, Integer year) {
        sendMessage.setText(Constants.HELLO_I_AM_A_BOT_THAT_WILL_HELP_YOU_FIND_EVENTS_IN_BALI);
        sendMessage.setReplyMarkup(KeyboardUtil.getKeyboard(month, year));
    }

    // Метод для загрузки дополнительной информации с сервера
    private String loadAdditionalInfo() {
        // Ваш код для загрузки дополнительной информации с сервера
        return "Это дополнительная информация";
    }
}
