package com.balievent.telegrambot.service;

import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.service.handler.callback.AbstractShowHandler;
import com.balievent.telegrambot.service.handler.callback.ShowLessHandler;
import com.balievent.telegrambot.service.handler.callback.ShowMoreHandler;
import com.balievent.telegrambot.service.handler.common.MediaHandler;
import com.balievent.telegrambot.service.handler.common.SendShowMoreMessageHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandlerType;
import com.balievent.telegrambot.service.storage.MessageDataStorage;
import com.balievent.telegrambot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {
    private final Map<TextMessageHandlerType, TextMessageHandler> textMessageHandlers;
    private final MediaHandler mediaHandler;
    private final MessageDataStorage messageDataStorage;
    private final SendShowMoreMessageHandler sendShowMoreMessageHandler;
    private final AbstractShowHandler showLessHandler;
    private final AbstractShowHandler showMoreHandler;

    public MyTelegramBot(
        final MediaHandler mediaHandler,
        final MessageDataStorage messageDataStorage,
        final SendShowMoreMessageHandler sendShowMoreMessageHandler,
        final Map<TextMessageHandlerType, TextMessageHandler> textMessageHandlers,
        final ShowLessHandler showLessHandler,
        final ShowMoreHandler showMoreHandler) {
        super("6781420399:AAHi0vGFUPnh-7wBzC7si7hw1XRQmrNmPzA");
        this.mediaHandler = mediaHandler;
        this.messageDataStorage = messageDataStorage;
        this.sendShowMoreMessageHandler = sendShowMoreMessageHandler;
        this.textMessageHandlers = textMessageHandlers;
        this.showLessHandler = showLessHandler;
        this.showMoreHandler = showMoreHandler;

    }

    @Override
    public String getBotUsername() {
        return "BaliEventsCoordinatebot";
    }

    @Override
    public void onUpdateReceived(final Update update) {
        try {
            if (update.hasCallbackQuery()) {
                processCallbackQuery(update);
            } else {
                processTextMessage(update);
            }
        } catch (TelegramApiException e) {
            log.info("Failed to process update", e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Обработка текстовых сообщений от пользователя
     *
     * @param update - все возможные события от пользователя
     * @throws TelegramApiException - отдает сообщение пользователю по его запросу
     */
    private void processTextMessage(final Update update) throws TelegramApiException {
        if (update.getMessage().getText().contains("/start")) { // обработчик команды /start
            // Обработчик класс StartCommandHandler
            execute(textMessageHandlers.get(TextMessageHandlerType.START_COMMAND).handle(update));
            executeSendShowMoreMessage(update, MyConstants.SHOW_FULL_MONTH);

        } else if (DateUtil.isCalendarMonthChanged(update.getMessage().getText())) { // обработчик изменения месяца в календаре
            // Обработчик класс CalendarMonthChangedHandler
            execute(textMessageHandlers.get(TextMessageHandlerType.CALENDAR_MONTH_CHANGED).handle(update));
            executeSendShowMoreMessage(update, MyConstants.SHOW_FULL_MONTH);

        } else if (DateUtil.isDateSelected(update.getMessage().getText())) { // обработчик выбора даты
            // Обработчик класс DateSelectedHandler
            execute(textMessageHandlers.get(TextMessageHandlerType.DATE_SELECTED).handle(update));
            executeSendMedia(update);
            executeSendShowMoreMessage(update, MyConstants.SHOW_MORE);

        } else { // обработчик непонятных сообщений
            // Обработчик класс MisUnderstandingMessageHandler
            execute(textMessageHandlers.get(TextMessageHandlerType.MIS_UNDERSTANDING_MESSAGE).handle(update));
        }
    }

    /**
     * Обработка сообщений от нажатых кнопок
     *
     * @param update - все возможные события от пользователя
     * @throws TelegramApiException - ошибка
     */
    private void processCallbackQuery(final Update update) throws TelegramApiException {
        final String callbackData = update.getCallbackQuery().getData();

        if (callbackData.contains(MyConstants.SHOW_MORE)) {
            execute(showMoreHandler.handle(update));
        } else if (callbackData.contains(MyConstants.SHOW_LESS)) {
            execute(showLessHandler.handle(update));
        } else if (callbackData.contains(MyConstants.SHOW_FULL_MONTH)) {
            execute(showMoreHandler.handle(update));
        } else if (callbackData.contains(MyConstants.SHOW_SHORT_MONTH)) {
            execute(showLessHandler.handle(update));
        }
    }

    private void executeSendShowMoreMessage(final Update update, final String callbackName) throws TelegramApiException {
        if (mediaHandler.findEventPhotosForUserDate(update).isEmpty()) {
            return;
        }
        final Message messageExecute = execute(sendShowMoreMessageHandler.handle(update, callbackName));
        messageDataStorage.addUserMessageData(messageExecute, update);
    }

    private void executeSendMedia(final Update update) {
        try {
            final List<InputMediaPhoto> eventPhotos = mediaHandler.findEventPhotosForUserDate(update);
            if (eventPhotos.size() == 1) {
                execute(mediaHandler.handleSingleMedia(update, eventPhotos));
            } else {
                final List<SendMediaGroup> sendMediaGroups = mediaHandler.handleMultipleMedia(update, eventPhotos);
                for (SendMediaGroup sendMediaGroup : sendMediaGroups) {
                    execute(sendMediaGroup);
                }
            }
        } catch (TelegramApiException e) {
            log.error("Failed to send media", e);
        }
    }

}

