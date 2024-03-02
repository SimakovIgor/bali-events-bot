package com.balievent.telegrambot.service;

import com.balievent.telegrambot.configuration.TelegramBotProperties;
import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.service.handler.callback.CallbackHandler;
import com.balievent.telegrambot.service.handler.callback.CallbackHandlerMessageType;
import com.balievent.telegrambot.service.handler.common.MediaHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandlerType;
import com.balievent.telegrambot.service.storage.MessageDataStorage;
import com.balievent.telegrambot.service.storage.UserDataStorage;
import com.balievent.telegrambot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
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
    private final Map<CallbackHandlerMessageType, CallbackHandler> callbackHandlers;
    private final TelegramBotProperties telegramBotProperties;
    private final MessageDataStorage messageDataStorage;
    private final UserDataStorage userDataStorage;
    private final MediaHandler mediaHandler;

    public MyTelegramBot(
        final MediaHandler mediaHandler,
        final MessageDataStorage messageDataStorage,
        final Map<TextMessageHandlerType, TextMessageHandler> textMessageHandlers,
        final Map<CallbackHandlerMessageType, CallbackHandler> callbackHandlers,
        final TelegramBotProperties telegramBotProperties,
        final UserDataStorage userDataStorage
    ) {
        super(telegramBotProperties.getToken());
        this.mediaHandler = mediaHandler;
        this.messageDataStorage = messageDataStorage;
        this.textMessageHandlers = textMessageHandlers;
        this.telegramBotProperties = telegramBotProperties;
        this.userDataStorage = userDataStorage;
        this.callbackHandlers = callbackHandlers;
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getUsername();
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
        final Long chatId = update.getMessage().getChatId();
        if (update.getMessage().getText().contains("/start")) { // обработчик команды /start
            // Обработчик класс StartCommandHandler
            execute(textMessageHandlers.get(TextMessageHandlerType.START_COMMAND).handle(update));
            executeSendShowMoreMessage(update, chatId);

        } else if (DateUtil.isCalendarMonthChanged(update.getMessage().getText())) { // обработчик изменения месяца в календаре
            // Обработчик класс CalendarMonthChangedHandler
            execute(textMessageHandlers.get(TextMessageHandlerType.CALENDAR_MONTH_CHANGED).handle(update));
            executeSendShowMoreMessage(update, chatId);

        } else if (DateUtil.isDateSelected(update.getMessage().getText())) { // обработчик выбора даты
            processDateSelected(update, chatId);
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
        final Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        if (callbackData.contains(MyConstants.SHOW_MORE) || callbackData.contains(MyConstants.SHOW_FULL_MONTH)) {
            // Обработчик класс ShowMoreHandler
            execute(callbackHandlers.get(CallbackHandlerMessageType.SHOW_MORE).handle(update));
        } else if (callbackData.contains(MyConstants.SHOW_LESS) || callbackData.contains(MyConstants.SHOW_SHORT_MONTH)) {
            // Обработчик класс ShowLessHandler
            execute(callbackHandlers.get(CallbackHandlerMessageType.SHOW_LESS).handle(update));
        } else if (callbackData.contains("next_pagination")) {
            // Обработчик класс NextPaginationHandler
            execute(callbackHandlers.get(CallbackHandlerMessageType.NEXT_PAGINATION).handle(update));
            updateMedia(callbackChatId);
        } else if (callbackData.contains("previous_pagination")) {
            // Обработчик класс PreviousPaginationHandler
            execute(callbackHandlers.get(CallbackHandlerMessageType.PREVIOUS_PAGINATION).handle(update));
            updateMedia(callbackChatId);
        }
    }

    /**
     * Обновление медиафайлов в чате пользователя (Удаление и создание новых)
     *
     * @param chatId - идентификатор чата
     * @throws TelegramApiException - ошибка
     */
    private void updateMedia(final Long chatId) throws TelegramApiException {
        removeMediaMessage(chatId);
        executeSendMedia(chatId);
    }

    /**
     * Отправка сообщения "Показать еще" пользователю
     *
     * @param update - все возможные события от пользователя
     * @param chatId - идентификатор чата
     * @throws TelegramApiException - ошибка
     */
    private void executeSendShowMoreMessage(final Update update, final Long chatId) throws TelegramApiException {
        final SendMessage sendMessage = textMessageHandlers.get(TextMessageHandlerType.SEND_SHOW_MORE_MESSAGE).handle(update);
        final Message messageExecute = execute(sendMessage);
        messageDataStorage.addUserMessageData(messageExecute, chatId);
    }

    /**
     * Отправка медиафайлов пользователю в зависимости от количества найденных фотографий
     *
     * @param chatId - идентификатор чата
     */
    private void executeSendMedia(final Long chatId) {
        try {
            final List<InputMediaPhoto> eventPhotos = mediaHandler.findEventPhotos(chatId);
            if (eventPhotos.size() == 1) {
                execute(mediaHandler.handleSingleMedia(chatId, eventPhotos));
            } else if (eventPhotos.size() > 1) {
                final SendMediaGroup sendMediaGroup = mediaHandler.handleMultipleMedia(chatId, eventPhotos);
                final List<Message> messageList = execute(sendMediaGroup);
                //Сохраняем для дальнейшей очистки сообщений
                userDataStorage.saveMediaIdList(messageList, chatId);
            }
        } catch (TelegramApiException e) {
            log.error("Failed to send media", e);
        }
    }

    /**
     * Обработка выбора даты пользователем
     *
     * @param update - все возможные события от пользователя
     * @param chatId - идентификатор чата
     * @throws TelegramApiException - ошибка
     */
    private void processDateSelected(final Update update, final Long chatId) throws TelegramApiException {
        removeLastDateSelectedMessageIfExist(chatId);
        // Обработчик класс DateSelectedHandler
        final Message message = execute(textMessageHandlers.get(TextMessageHandlerType.DATE_SELECTED).handle(update));
        userDataStorage.saveLastDateSelectedMessageId(message.getMessageId(), chatId);

        executeSendMedia(chatId);
    }

    /**
     * Удаление последнего сообщения со списком ивентов на определенную дату , если оно существует
     *
     * @param chatId - идентификатор чата
     * @throws TelegramApiException - ошибка
     */
    private void removeLastDateSelectedMessageIfExist(final Long chatId) throws TelegramApiException {
        final List<Integer> messageIds = userDataStorage.getAllMessageIdsForDelete(chatId);
        if (!CollectionUtils.isEmpty(messageIds)) {
            execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(messageIds)
                .build());
        }
    }

    /**
     * Удаление последних отправленных медиафайлов в чате пользователя
     *
     * @param chatId - идентификатор чата
     * @throws TelegramApiException - ошибка
     */
    private void removeMediaMessage(final Long chatId) throws TelegramApiException {
        final List<Integer> mediaIdList = userDataStorage.getUserData(chatId).getMediaIdList();
        if (!CollectionUtils.isEmpty(mediaIdList)) {
            execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(mediaIdList)
                .build());
        }

    }
}

