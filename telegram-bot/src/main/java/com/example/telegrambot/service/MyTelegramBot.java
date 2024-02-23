package com.example.telegrambot.service;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.util.DateUtil;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {

    private final CalendarProcessService calendarProcessService;
    private final ImageProcessService imageProcessService;
    private final CalendarStoreService calendarStoreService;
    private final MessageStorage messageStorage = new MessageStorage();

    public MyTelegramBot(final CalendarProcessService calendarProcessService,
                         final ImageProcessService imageProcessService,
                         final CalendarStoreService calendarStoreService) {
        super("6781420399:AAHi0vGFUPnh-7wBzC7si7hw1XRQmrNmPzA");

        this.calendarProcessService = calendarProcessService;
        this.imageProcessService = imageProcessService;
        this.calendarStoreService = calendarStoreService;
    }

    @Override
    public String getBotUsername() {
        return "BaliEventsCoordinatebot";
    }

    @Override
    public void onUpdateReceived(final Update update) {
        try {
            if (update.hasCallbackQuery()) { // Обработка нажатия кнопки "Show more"

                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final String callbackData = callbackQuery.getData();
                if (callbackData.contains(MyConstants.SHOW_MORE)) {
                    final EditMessageText editMessageText = calendarProcessService.processDig(update, messageStorage);
                    execute(editMessageText);
                } else if (callbackData.contains(MyConstants.SHOW_LESS)) {
                    final EditMessageText editMessageText = calendarProcessService.processFew(update, messageStorage);
                    execute(editMessageText);
                }
            } else {
                final String text = update.getMessage().getText();

                if (text.contains("/start")) {
                    calendarStoreService.put(update);
                    execute(getStartMessage(update, calendarStoreService.get(update)));
                } else if (DateUtil.isCalendarMonthChanged(text)) {
                    calendarStoreService.updateWithCalendarMonthChanged(update);
                    execute(getCalendarMonthChangedMessage(update));
                } else if (DateUtil.isDateSelected(text)) {
                    final LocalDate localDate = calendarStoreService.updateWithSelectedDate(update);
                    execute(calendarProcessService.processShort(update, localDate));
                    execute(calendarProcessService.process(update, localDate));
                    executeSendMediaGroup(update, localDate);
                    final Message messageExecute = execute(getSignature(update, messageStorage));  // сообщение сообщение Show_More
                    messageStorage.addUser(messageExecute, update, localDate, messageStorage);          // сохраняем номер третьего сообщения
                } else {
                    execute(getMisUnderstandingMessage(update));
                }
            }
        } catch (TelegramApiException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void executeSendMediaGroup(final Update update, final LocalDate localDate) {
        imageProcessService.process(update, localDate)
            .forEach(sendMediaGroup -> {
                try {
                    execute(sendMediaGroup);
                } catch (TelegramApiException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            });
    }

    private SendMessage getStartMessage(final Update update, final LocalDate localDate) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MyConstants.HELLO_I_AM_A_BOT_THAT_WILL_HELP_YOU_FIND_EVENTS_IN_BALI)
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear())) // календарь
            .build();
    }

    private SendMessage getMisUnderstandingMessage(final Update update) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MyConstants.THIS_WORD_IS_NOT_RESERVED + update.getMessage().getText() + MyConstants.LIST_OF_RESERVED_WORDS_HELP)
            .build();
    }

    private SendMessage getSignature(final Update update, final MessageStorage messageStorage) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MyConstants.LIST_OF_MORE)
            .replyMarkup(KeyboardUtil.setNewButton(MyConstants.SHOW_MORE_TEXT, MyConstants.SHOW_MORE, update, messageStorage))   // Название кнопки
            .build();
    }

    private SendMessage getCalendarMonthChangedMessage(final Update update) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(Constants.CHOOSE_DATE_OR_INSERT)
            .build();
    }

    private int getMessageIdFromCallbackData(final String callbackData) throws NumberFormatException {
        final String[] parts = callbackData.split(MyConstants.SHOW_SEPARATOR);
        if (parts.length < 2 || parts[1].isEmpty()) {
            throw new NumberFormatException("Invalid callback data format");
        }

        return Integer.parseInt(parts[1]);
    }
}

