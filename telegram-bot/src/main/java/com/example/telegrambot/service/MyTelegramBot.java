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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {

    private final CalendarProcessService calendarProcessService;
    private final ImageProcessService imageProcessService;
    private final CalendarStoreService calendarStoreService;
    private final MessageDataStorage messageDataStorage;

    public MyTelegramBot(final CalendarProcessService calendarProcessService,
                         final ImageProcessService imageProcessService,
                         final CalendarStoreService calendarStoreService,
                         final MessageDataStorage messageDataStorage) {
        super("6781420399:AAHi0vGFUPnh-7wBzC7si7hw1XRQmrNmPzA");

        this.calendarProcessService = calendarProcessService;
        this.imageProcessService = imageProcessService;
        this.calendarStoreService = calendarStoreService;
        this.messageDataStorage = messageDataStorage;
    }

    @Override
    public String getBotUsername() {
        return "BaliEventsCoordinatebot";
    }

    /***
     * начало программы для
     *
     * @param update - событие из телеграмма
     */
    @Override
    public void onUpdateReceived(final Update update) {
        try {
            if (update.hasCallbackQuery()) {

                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final String callbackData = callbackQuery.getData();
                if (callbackData.contains(MyConstants.SHOW_MORE)) {
                    final EditMessageText editMessageText = calendarProcessService.processShowMore(update, messageDataStorage);
                    execute(editMessageText);
                } else if (callbackData.contains(MyConstants.SHOW_LESS)) {
                    final EditMessageText editMessageText = calendarProcessService.processShowLess(update, messageDataStorage);
                    execute(editMessageText);
                }
            } else {
                final String text = update.getMessage().getText();

                if (text.contains("/start")) {
                    calendarStoreService.put(update);
                    execute(getStartMessage(update, calendarStoreService.get(update)));
                } else if (DateUtil.isCalendarMonthChanged(text)) {
                    final LocalDate localDate = calendarStoreService.updateWithCalendarMonthChanged(update);
                    execute(getCalendarMonthChangedMessage(update, localDate));
                } else if (DateUtil.isDateSelected(text)) {
                    final LocalDate localDate = calendarStoreService.updateWithSelectedDate(update);
                    execute(calendarProcessService.processShort(update, localDate));
                    executeSendMediaGroup(update, localDate);
                    final Message messageExecute = execute(getSignature(update));
                    messageDataStorage.addUserMessageData(messageExecute, update, localDate);
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
            .text(String.format("Это слово(а) не зарезервировано: %s Список зарезервированных слов /help ", update.getMessage().getText()))
            .build();
    }

    private SendMessage getSignature(final Update update) {
        final String chatId = update.getMessage().getChatId().toString();
        final Long nextMessageNumber = messageDataStorage.calculateNextMessageId(chatId);
        final InlineKeyboardMarkup replyMarkup = KeyboardUtil.setNewButton(nextMessageNumber);
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MyConstants.LIST_OF_MORE)
            .replyMarkup(replyMarkup)
            .build();
    }

    private SendMessage getCalendarMonthChangedMessage(final Update update, final LocalDate localDate) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MyConstants.CHOOSE_DATE_OR_INSERT)
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear()))
            .build();
    }
}

