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

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {

    private final CalendarProcessService calendarProcessService;
    private final ImageProcessService imageProcessService;
    private final CalendarStoreService calendarStoreService;

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
            final String text = update.getMessage().getText();

            if (text.contains("/start")) {
                calendarStoreService.put(update);
                execute(getStartMessage(update, calendarStoreService.get(update)));
            } else if (DateUtil.isCorrectDateFormat(text)) {
                final LocalDate localDate = calendarStoreService.putOrUpdate(update);
                execute(calendarProcessService.process(update, localDate));
                executeSendMediaGroup(update, localDate);
            } else {
                execute(getMisUnderstandingMessage(update));
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
            .text(Constants.HELLO_I_AM_A_BOT_THAT_WILL_HELP_YOU_FIND_EVENTS_IN_BALI)
            .replyMarkup(KeyboardUtil.getKeyboard(localDate.getMonthValue(), localDate.getYear()))
            .build();
    }

    private SendMessage getMisUnderstandingMessage(final Update update) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(Constants.THIS_WORD_IS_NOT_RESERVED + update.getMessage().getText() + Constants.LIST_OF_RESERVED_WORDS_HELP)
            .build();
    }

}
