package com.balievent.telegrambot.service;

import com.balievent.telegrambot.configuration.TelegramBotProperties;
import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandlerType;
import com.balievent.telegrambot.util.DateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {
    private final Map<TextMessageHandlerType, TextMessageHandler> textMessageHandlers;
    private final Map<CallbackHandlerType, ButtonCallbackHandler> callbackHandlers;
    private final TelegramBotProperties telegramBotProperties;

    public MyTelegramBot(
        final @Lazy Map<TextMessageHandlerType, TextMessageHandler> textMessageHandlers,
        final @Lazy Map<CallbackHandlerType, ButtonCallbackHandler> callbackHandlers,
        final TelegramBotProperties telegramBotProperties
    ) {
        super(telegramBotProperties.getToken());
        this.textMessageHandlers = textMessageHandlers;
        this.telegramBotProperties = telegramBotProperties;
        this.callbackHandlers = callbackHandlers;
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getUsername();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(final Update update) {
        try {
            if (update.hasCallbackQuery()) {
                processCallbackQuery(update);
            } else {
                processTextMessage(update);
            }
        } catch (ServiceException e) {
            log.error("ServiceException {}", e.getMessage(), e);
            execute(SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(e.getMessage())
                .build());
        }
    }

    private void processTextMessage(final Update update) throws TelegramApiException {
        final String messageText = update.getMessage().getText();
        if (messageText.contains("/start")) {
            textMessageHandlers.get(TextMessageHandlerType.START_COMMAND).handle(update);
            return;
        }

        if (DateUtil.isCalendarMonthChanged(messageText)) {
            textMessageHandlers.get(TextMessageHandlerType.CALENDAR_MONTH_CHANGED).handle(update);
        } else if (DateUtil.isDateSelected(messageText)) {
            textMessageHandlers.get(TextMessageHandlerType.DATE_SELECTED).handle(update);
        } else {
            execute(DeleteMessage.builder()
                .chatId(update.getMessage().getChatId())
                .messageId(update.getMessage().getMessageId())
                .build());
        }
    }

    private void processCallbackQuery(final Update update) throws TelegramApiException {
        final String clickedButtonName = update.getCallbackQuery().getData().toUpperCase(Locale.ROOT);

        final CallbackHandlerType callbackHandlerType = TelegramButton.valueOf(clickedButtonName).getCallbackHandlerType();
        callbackHandlers.get(callbackHandlerType).handle(update);
    }

}

