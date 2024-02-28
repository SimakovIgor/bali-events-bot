package com.example.telegrambot.service;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.service.handler.callback.ShowLessHandler;
import com.example.telegrambot.service.handler.callback.ShowMoreHandler;
import com.example.telegrambot.service.handler.media.MediaHandler;
import com.example.telegrambot.service.handler.textmessage.CalendarMonthChangedHandler;
import com.example.telegrambot.service.handler.textmessage.DateSelectedHandler;
import com.example.telegrambot.service.handler.textmessage.MisUnderstandingMessageHandler;
import com.example.telegrambot.service.handler.textmessage.SendShowMoreMessageHandler;
import com.example.telegrambot.service.handler.textmessage.StartCommandHandler;
import com.example.telegrambot.service.storage.CalendarDataStorage;
import com.example.telegrambot.service.storage.MessageDataStorage;
import com.example.telegrambot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
//todo: Избавиться от большого количества зависимостей в одном сервисе
public class MyTelegramBot extends TelegramLongPollingBot {
    private final MediaHandler mediaHandler;
    private final CalendarDataStorage calendarDataStorage;
    private final MessageDataStorage messageDataStorage;
    private final StartCommandHandler startCommandHandler;
    private final CalendarMonthChangedHandler calendarMonthChangedHandler;
    private final DateSelectedHandler dateSelectedHandler;
    private final ShowMoreHandler showMoreHandler;
    private final ShowLessHandler showLessHandler;
    private final MisUnderstandingMessageHandler misUnderstandingMessageHandler;
    private final SendShowMoreMessageHandler sendShowMoreMessageHandler;

    public MyTelegramBot(
        final MediaHandler mediaHandler,
        final CalendarDataStorage calendarDataStorage,
        final MessageDataStorage messageDataStorage,
        final StartCommandHandler startCommandHandler,
        final CalendarMonthChangedHandler calendarMonthChangedHandler,
        final DateSelectedHandler dateSelectedHandler,
        final ShowMoreHandler showMoreHandler,
        final ShowLessHandler showLessHandler,
        final MisUnderstandingMessageHandler misUnderstandingMessageHandler,
        final SendShowMoreMessageHandler sendShowMoreMessageHandler) {
        super("6781420399:AAHi0vGFUPnh-7wBzC7si7hw1XRQmrNmPzA");
        this.mediaHandler = mediaHandler;
        this.calendarDataStorage = calendarDataStorage;
        this.messageDataStorage = messageDataStorage;
        this.startCommandHandler = startCommandHandler;
        this.calendarMonthChangedHandler = calendarMonthChangedHandler;
        this.dateSelectedHandler = dateSelectedHandler;
        this.showMoreHandler = showMoreHandler;
        this.showLessHandler = showLessHandler;
        this.misUnderstandingMessageHandler = misUnderstandingMessageHandler;
        this.sendShowMoreMessageHandler = sendShowMoreMessageHandler;
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
        if (update.getMessage().getText().contains("/start")) {  // обработчик команды /start
            execute(startCommandHandler.handleTextMessage(update));

        } else if (DateUtil.isCalendarMonthChanged(update.getMessage().getText())) { // обработчик изменения месяца
            execute(calendarMonthChangedHandler.handleTextMessage(update));
            executeSendShowMoreMessage(update, MyConstants.SHOW_FULL_MONTH);

        } else if (DateUtil.isDateSelected(update.getMessage().getText())) { // обработчик выбора даты
            execute(dateSelectedHandler.handleTextMessage(update));
            executeSendMedia(update);
            executeSendShowMoreMessage(update, MyConstants.SHOW_MORE);

        } else { // обработчик непонятных сообщений
            execute(misUnderstandingMessageHandler.handleTextMessage(update));
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
        if (callbackData.contains(MyConstants.SHOW_MORE)) {  // обработчик нажатия кнопки "Показать еще"
            execute(showMoreHandler.handleCallbackQuery(update, MyConstants.SHOW_MORE));
        } else if (callbackData.contains(MyConstants.SHOW_LESS)) { // обработчик нажатия кнопки "Показать меньше"
            execute(showLessHandler.handleCallbackQuery(update, MyConstants.SHOW_MORE));
        } else if (callbackData.contains(MyConstants.SHOW_FULL_MONTH)) {
            execute(showMoreHandler.handleCallbackQuery(update, MyConstants.SHOW_SHORT_MONTH));
        } else if (callbackData.contains(MyConstants.SHOW_SHORT_MONTH)) {
            execute(showLessHandler.handleCallbackQuery(update, MyConstants.SHOW_FULL_MONTH));
        }
    }

    private void executeSendShowMoreMessage(final Update update, final String showFullMonth) throws TelegramApiException {
        final Message messageExecute = execute(sendShowMoreMessageHandler.handleTextMessage(update, showFullMonth));
        messageDataStorage.addUserMessageData(messageExecute, update);
    }

    private void executeSendMedia(final Update update) {
        try {
            final LocalDate localDate = calendarDataStorage.get(update);
            final List<InputMediaPhoto> eventPhotos = mediaHandler.findEventPhotos(localDate);
            if (eventPhotos.size() == 1) {
                execute(mediaHandler.processSingleMedia(update, eventPhotos));
            } else {
                final List<SendMediaGroup> sendMediaGroups = mediaHandler.processMultipleMedia(update, eventPhotos);
                for (SendMediaGroup sendMediaGroup : sendMediaGroups) {
                    execute(sendMediaGroup);
                }
            }
        } catch (TelegramApiException e) {
            log.error("Failed to send media", e);
        }
    }

}

