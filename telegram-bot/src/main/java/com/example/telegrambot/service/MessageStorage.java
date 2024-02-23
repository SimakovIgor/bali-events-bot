/**
 * Создал Андрей Антонов 2/21/2024 5:42 PM.
 **/

package com.example.telegrambot.service;

import com.example.telegrambot.model.entity.ShowMoreData;
import com.example.telegrambot.model.entity.ShowMorePrimaryKey;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageStorage {
    private final Map<ShowMorePrimaryKey, ShowMoreData> storage = new ConcurrentHashMap<>();

    public void addUser(final Message sentMessage,
                        final Update update,
                        final LocalDate timestamp) {

        //id = chatId + MessageNumber
        final String chatId = update.getMessage().getChatId().toString();     // Получаем идентификатор пользователя
        final Long nextMessageNumber = getNextMessageNumber(chatId);           // Получаем МОЙ номер сообщения для этого пользователя, который мы отправили в сообщение
        final String messageId = sentMessage.getMessageId().toString();       // Получить идентификатор отправленного сообщения

        // Сохраняем: chatId    - Id чата,
        // nextMessageNumber    - МОЙ Id сообщения для этого чата,
        // messageId            - Id отправленного сообщения
        // timestamp            - дату запроса для этого сообщения
        addMessage(chatId, nextMessageNumber, messageId, timestamp);
    }

    public void addMessage(final String chatId,
                           final Long messageNumber,
                           final String messageId,
                           final LocalDate timestamp) {
        final ShowMorePrimaryKey key = ShowMorePrimaryKey.builder()
            .chatId(chatId)
            .messageNumber(messageNumber)
            .build();
        final ShowMoreData showMoreData = ShowMoreData.builder()
            .messageId(messageId)
            .timestamp(timestamp)
            .build();

        if (!storage.containsKey(key)) {
            storage.put(key, showMoreData);
        }
    }

    // получаем следующий свободный идентификатор сообщения для данного пользователя
    public Long getNextMessageNumber(final String chatId) {
        return storage.keySet().stream()
            .filter(key -> key.getChatId().equals(chatId))
            .map(ShowMorePrimaryKey::getMessageNumber)
            .max(Long::compareTo)
            .map(number -> number + 1)
            .orElse(1L);
    }

    public String getMessageId(final String chatId, final Long messageNumber) {
        final ShowMorePrimaryKey key = ShowMorePrimaryKey.builder()
            .chatId(chatId)
            .messageNumber(messageNumber)
            .build();
        return storage.get(key).getMessageId();
    }

    // получаем дату запроса по данному пользователю и номеру сообщения
    public LocalDate getLocalDate(final String chatId, final Long messageNumber) {
        final ShowMorePrimaryKey key = ShowMorePrimaryKey.builder()
            .chatId(chatId)
            .messageNumber(messageNumber)
            .build();
        return storage.get(key).getTimestamp();
    }

}
