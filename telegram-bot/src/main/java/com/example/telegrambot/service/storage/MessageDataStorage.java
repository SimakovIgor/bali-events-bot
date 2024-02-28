/**
 * Создал Андрей Антонов 2/21/2024 5:42 PM.
 **/

package com.example.telegrambot.service.storage;

import com.example.telegrambot.model.entity.MessageData;
import com.example.telegrambot.model.entity.MessagePrimaryKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MessageDataStorage {
    private final Map<MessagePrimaryKey, MessageData> messageDataMap = new ConcurrentHashMap<>();
    private final CalendarDataStorage calendarDataStorage;

    private static MessagePrimaryKey getMessagePrimaryKey(final String chatId, final Long messageNumber) {
        return MessagePrimaryKey.builder()
            .chatId(chatId)
            .messageNumber(messageNumber)
            .build();
    }

    public void addUserMessageData(final Message sentMessage,
                                   final Update update) {
        final LocalDate timestamp = calendarDataStorage.get(update);

        final String chatId = update.getMessage().getChatId().toString();
        final Long nextMessageNumber = calculateNextMessageId(chatId);
        final String messageId = sentMessage.getMessageId().toString();

        addMessage(chatId, nextMessageNumber, messageId, timestamp);
    }

    /**
     * Добавляем сообщение в хранилище
     *
     * @param chatId        - Id чата
     * @param messageNumber - МОЙ Id сообщения для этого чата
     * @param messageId     - Id отправленного сообщения
     * @param timestamp     - дату запроса для этого сообщения
     */
    public void addMessage(final String chatId,
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
    public Long calculateNextMessageId(final String chatId) {
        return messageDataMap.keySet().stream()
            .filter(key -> key.getChatId().equals(chatId))
            .map(MessagePrimaryKey::getMessageNumber)
            .max(Long::compareTo)
            .map(number -> number + 1)
            .orElse(1L);
    }

    public String getMessageTimestamp(final String chatId, final Long messageNumber) {
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
    public LocalDate getLocalDate(final String chatId, final Long messageNumber) {
        final MessagePrimaryKey key = getMessagePrimaryKey(chatId, messageNumber);
        return messageDataMap.get(key).getTimestamp();
    }

}
