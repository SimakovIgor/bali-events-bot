/**
 * Создал Андрей Антонов 2/21/2024 5:42 PM.
 **/

package com.balievent.telegrambot.service.storage;

import com.balievent.telegrambot.exceptions.ErrorCode;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.entity.MessageData;
import com.balievent.telegrambot.model.entity.MessagePrimaryKey;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MessageDataStorage {
    private final Map<MessagePrimaryKey, MessageData> messageDataMap = new ConcurrentHashMap<>();
    private final UserDataRepository userDataRepository;

    private static MessagePrimaryKey getMessagePrimaryKey(final Long chatId, final Long messageNumber) {
        return MessagePrimaryKey.builder()
            .chatId(chatId)
            .messageNumber(messageNumber)
            .build();
    }

    public void addUserMessageData(final Message sentMessage,
                                   final Long chatId) {
        final UserData userData = userDataRepository.findById(chatId)
            .orElseThrow(() -> new ServiceException(ErrorCode.ERR_CODE_001));

        final Long nextMessageNumber = calculateNextMessageId(chatId);
        final String messageId = sentMessage.getMessageId().toString();

        addMessage(chatId, nextMessageNumber, messageId, userData.getCalendarDate());
    }

    /**
     * Добавляем сообщение в хранилище
     *
     * @param chatId        - Id чата
     * @param messageNumber - МОЙ Id сообщения для этого чата
     * @param messageId     - Id отправленного сообщения
     * @param timestamp     - дату запроса для этого сообщения
     */
    public void addMessage(final Long chatId,
                           final Long messageNumber,
                           final String messageId,
                           final LocalDate timestamp) {
        final MessagePrimaryKey key = getMessagePrimaryKey(chatId, messageNumber);
        final MessageData messageData = MessageData.builder()
            .messageId(messageId)
            .timestamp(timestamp)
            .build();

        messageDataMap.computeIfAbsent(key, k -> messageData);
    }

    /**
     * Получаем следующий свободный идентификатор сообщения для данного пользователя.
     *
     * @param chatId - id chat
     * @return - Следующий свободный идентификатор сообщения
     */
    public Long calculateNextMessageId(final Long chatId) {
        return messageDataMap.keySet().stream()
            .filter(key -> key.getChatId().equals(chatId))
            .map(MessagePrimaryKey::getMessageNumber)
            .max(Long::compareTo)
            .map(number -> number + 1)
            .orElse(1L);
    }

    public String getMessageTimestamp(final Long chatId, final Long messageNumber) {
        final MessagePrimaryKey key = getMessagePrimaryKey(chatId, messageNumber);
        return messageDataMap.get(key).getMessageId();
    }

    /**
     * Получаем дату запроса по данному пользователю и номеру сообщения
     *
     * @param chatId        - id chat
     * @param messageNumber - номер сообщения
     * @return - дата запроса
     */
    public LocalDate getLocalDate(final Long chatId, final Long messageNumber) {
        final MessagePrimaryKey key = getMessagePrimaryKey(chatId, messageNumber);
        return messageDataMap.get(key).getTimestamp();
    }

}
