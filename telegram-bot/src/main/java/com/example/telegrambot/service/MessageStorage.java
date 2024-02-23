/**
 * Создал Андрей Антонов 2/21/2024 5:42 PM.
 **/

package com.example.telegrambot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageStorage {
    private final Map<String, Map<Integer, Map<String, LocalDate>>> storage = new ConcurrentHashMap<>();

    public void addUser(final Message sentMessage, final Update update, final LocalDate timestamp, final MessageStorage messageStorage) {
        final String chatId = update.getMessage().getChatId().toString();     // Получаем идентификатор пользователя
        final int nextMessageNumber = getNextMessageNumber(chatId);           // Получаем МОЙ номер сообщения для этого пользователя, который мы отправили в сообщение
        final String messageId = sentMessage.getMessageId().toString();       // Получить идентификатор отправленного сообщения

        // Сохраняем: chatId    - Id чата,
        // nextMessageNumber    - МОЙ Id сообщения для этого чата,
        // messageId            - Id отправленного сообщения
        // timestamp            - дату запроса для этого сообщения
        messageStorage.addMessage(chatId, nextMessageNumber, messageId, timestamp);
    }

    public void addMessage(final String userId, final int messageNumber, final String messageId, final LocalDate timestamp) {
        if (!storage.containsKey(userId)) {
            storage.put(userId, new HashMap<>());
        }
        if (!storage.get(userId).containsKey(messageNumber)) {
            storage.get(userId).put(messageNumber, new HashMap<>());
        }
        storage.get(userId).get(messageNumber).put(messageId, timestamp);
    }

    // получаем следующий свободный идентификатор сообщения для данного пользователя
    public int getNextMessageNumber(final String userId) {
        final Map<Integer, Map<String, LocalDate>> userMessages = storage.getOrDefault(userId, new HashMap<>());
        final int maxMessageNumber = userMessages.keySet().stream().max(Integer::compareTo).orElse(0);
        return maxMessageNumber + 1;
    }

    public String getMessageId(final String userId, final int messageNumber) {
        final Map<String, LocalDate> messages = storage.getOrDefault(userId, new HashMap<>()).getOrDefault(messageNumber, new HashMap<>());
        return messages.keySet().stream().findFirst().orElse(null);
    }

    // получаем дату запроса по данному пользователю и номеру сообщения
    public LocalDate getLocalDate(final String userId, final int messageNumber) {
        final Map<Integer, Map<String, LocalDate>> userMessages = storage.getOrDefault(userId, new HashMap<>());
        final Map<String, LocalDate> messages = userMessages.getOrDefault(messageNumber, new HashMap<>());
        return messages.values().stream().findFirst().orElse(null);
    }

}
