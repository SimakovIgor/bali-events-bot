package com.balievent.telegrambot.service;

import com.balievent.telegrambot.configuration.TelegramBotProperties;
import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.CallbackHandler;
import com.balievent.telegrambot.service.handler.common.MediaHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandlerType;
import com.balievent.telegrambot.service.storage.UserDataService;
import com.balievent.telegrambot.util.DateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {
    private final Map<TextMessageHandlerType, TextMessageHandler> textMessageHandlers;
    private final Map<TelegramButton, CallbackHandler> callbackHandlers;
    private final TelegramBotProperties telegramBotProperties;
    private final UserDataService userDataService;
    private final MediaHandler mediaHandler;

    public MyTelegramBot(
        final MediaHandler mediaHandler,
        final Map<TextMessageHandlerType, TextMessageHandler> textMessageHandlers,
        final Map<TelegramButton, CallbackHandler> callbackHandlers,
        final TelegramBotProperties telegramBotProperties,
        final UserDataService userDataService
    ) {
        super(telegramBotProperties.getToken());
        this.mediaHandler = mediaHandler;
        this.textMessageHandlers = textMessageHandlers;
        this.telegramBotProperties = telegramBotProperties;
        this.userDataService = userDataService;
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

    /**
     * Обработка текстовых сообщений от пользователя
     *
     * @param update - все возможные события от пользователя
     * @throws TelegramApiException - отдает сообщение пользователю по его запросу
     */
    private void processTextMessage(final Update update) throws TelegramApiException {
        final Long chatId = update.getMessage().getChatId();

        if (update.getMessage().getText().contains("/start")) {
            final UserData userData = userDataService.createOrUpdateUserData(chatId);
            removeUserMessageList(chatId, userData);
            processStartCommand(update, userData);
        } else {
            final UserData userData = userDataService.getUserData(chatId);
            removeUserMessageList(chatId, userData);
            if (DateUtil.isCalendarMonthChanged(update.getMessage().getText())) {
                processCalendarMonthChanged(update, chatId, userData);
            } else if (DateUtil.isDateSelected(update.getMessage().getText())) {
                processDateSelected(update, chatId, userData);
            }
        }
        userDataService.saveUserMessageId(update.getMessage().getMessageId(), chatId);

    }

    private void processStartCommand(final Update update,
                                     final UserData userData) throws TelegramApiException {
        final Long chatId = update.getMessage().getChatId();
        // Обработчик класс StartCommandHandler
        removeLastStartMessage(chatId, userData);
        removeLastDateSelectedMessageIfExist(chatId, userData);
        removeMediaMessage(chatId, userData);

        final Message message = execute(textMessageHandlers.get(TextMessageHandlerType.START_COMMAND).handle(update));
        userDataService.saveStartMessageId(message.getMessageId(), chatId);
    }

    /**
     * Обработка сообщений от нажатых кнопок
     *
     * @param update - все возможные события от пользователя
     * @throws TelegramApiException - ошибка
     */
    private void processCallbackQuery(final Update update) throws TelegramApiException {
        final String callbackData = update.getCallbackQuery().getData();
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();
        final TelegramButton clickedButton = TelegramButton.valueOf(callbackData.toUpperCase(Locale.ROOT));

        execute(callbackHandlers.get(clickedButton).handle(update));

        final UserData userData = userDataService.getUserData(chatId);
        removeMediaMessage(chatId, userData);

        if (clickedButton.isIncludeMedia()) {
            updateMedia(chatId);
        }

    }

    private void updateMedia(final Long chatId) {
        executeSendMedia(chatId);
    }

    private void executeSendMedia(final Long chatId) {
        try {
            final List<InputMediaPhoto> eventPhotos = mediaHandler.findEventPhotos(chatId);
            if (eventPhotos.size() == 1) {
                log.info("Sending eventPhotos to chatId: {} size {}", chatId, eventPhotos.size());
                final Message message = execute(mediaHandler.handleSingleMedia(chatId, eventPhotos));
                userDataService.saveMediaIdList(List.of(message), chatId);

            } else if (eventPhotos.size() > 1) {
                log.info("Sending eventPhotos to chatId: {} size {}", chatId, eventPhotos.size());
                final SendMediaGroup sendMediaGroup = mediaHandler.handleMultipleMedia(chatId, eventPhotos);
                final List<Message> messageList = execute(sendMediaGroup);
                //Сохраняем для дальнейшей очистки сообщений
                userDataService.saveMediaIdList(messageList, chatId);
            }
        } catch (TelegramApiException e) {
            log.error("Failed to send media", e);
        }
    }

    private void processDateSelected(final Update update,
                                     final Long chatId,
                                     final UserData userData) throws TelegramApiException {
        removeLastStartMessage(chatId, userData);
        removeLastDateSelectedMessageIfExist(chatId, userData);
        removeLastCalendarMonthChangedMessageIfExist(chatId, userData);
        // Обработчик класс DateSelectedHandler
        final Message message = execute(textMessageHandlers.get(TextMessageHandlerType.DATE_SELECTED).handle(update));
        userDataService.saveLastDateSelectedMessageId(message.getMessageId(), chatId);

        updateMedia(chatId);
    }

    private void processCalendarMonthChanged(final Update update,
                                             final Long chatId,
                                             final UserData userData) throws TelegramApiException {
        removeLastStartMessage(chatId, userData);
        removeLastCalendarMonthChangedMessageIfExist(chatId, userData);
        removeLastDateSelectedMessageIfExist(chatId, userData);
        removeMediaMessage(chatId, userData);

        // Обработчик класс CalendarMonthChangedHandler
        final Message lastCalendarChangedMessageId = execute(textMessageHandlers.get(TextMessageHandlerType.CALENDAR_MONTH_CHANGED)
            .handle(update));
        final List<Integer> messageIds = List.of(lastCalendarChangedMessageId.getMessageId());
        userDataService.saveLastCalendarChangedMessageId(messageIds, chatId);
    }

    private void removeLastCalendarMonthChangedMessageIfExist(final Long chatId, final UserData userData) {
        try {
            execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(userData.getCalendarChangedMessageIds())
                .build());
        } catch (TelegramApiException e) {
            log.warn("Calendar month changed message not found", e);
        }
    }

    private void removeLastDateSelectedMessageIfExist(final Long chatId, final UserData userData) {
        final List<Integer> messageIds = userDataService.getAllMessageIdsForDelete(userData);
        if (CollectionUtils.isEmpty(messageIds)) {
            return;
        }
        try {
            execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(messageIds)
                .build());
        } catch (TelegramApiException e) {
            log.warn("Date selected message not found", e);
        }
    }

    private void removeMediaMessage(final Long chatId, final UserData userData) {
        if (CollectionUtils.isEmpty(userData.getMediaMessageIdList())) {
            return;
        }
        try {
            execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(userData.getMediaMessageIdList())
                .build());

        } catch (TelegramApiException e) {
            log.warn("Media message not found", e);
        }
    }

    private void removeLastStartMessage(final Long chatId,
                                        final UserData userData) {
        if (userData.getStartMessageId() == null) {
            return;
        }
        try {
            execute(DeleteMessage.builder()
                .chatId(chatId)
                .messageId(userData.getStartMessageId())
                .build());
        } catch (TelegramApiException e) {
            log.warn("User message not found", e);
        }

    }

    private void removeUserMessageList(final Long chatId, final UserData userData) {
        if (userData.getUserMessageId() == null) {
            return;
        }
        try {
            execute(DeleteMessage.builder()
                .chatId(chatId)
                .messageId(userData.getUserMessageId())
                .build());
        } catch (TelegramApiException e) {
            log.warn("User message not found", e);
        }

    }
}

