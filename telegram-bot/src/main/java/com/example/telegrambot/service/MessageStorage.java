/**
 * Создал Андрей Антонов 2/21/2024 5:42 PM.
 **/

package com.example.telegrambot.service;

import jakarta.annotation.PostConstruct;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MessageStorage {
    private static final MessageStorage MESSAGE_STORAGE = new MessageStorage();
    private Map<String, Map<Integer, Map<String, LocalDate>>> storage;

    public MessageStorage() {
        storage = new HashMap<>();
    }

    public static MessageStorage getMessageStorage() {
        return MESSAGE_STORAGE;
    }

    @PostConstruct
    public void init() {
        storage = new HashMap<>();
    }
    // Добавляем метод для получения экземпляра MessageStorage

    public void addUser(final Message sentMessage, final Update update, final LocalDate timestamp, final MessageStorage messageStorage) {
        final String userId = update.getMessage().getChatId().toString();     // Получаем идентификатор пользователя
        final int nextMessageNumber = getNextMessageNumber(userId);           // Получаем МОЙ номер сообщения для этого пользователя, который мы отправили в сообщение
        final String messageId = sentMessage.getMessageId().toString();       // Получить идентификатор отправленного сообщения

        // Сохраняем: userId    - Id чата,
        // nextMessageNumber    - МОЙ Id сообщения для этого чата,
        // messageId            - Id отправленного сообщения
        // timestamp            - дату запроса для этого сообщения
        messageStorage.addMessage(userId, nextMessageNumber, messageId, timestamp);
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

    /*    public static void main(final String[] args) {
        final MessageStorage messageStorage = MessageStorage.getMessageStorage();
        messageStorage.addMessage("user1", 1, "messageId1111", LocalDate.now());
        messageStorage.addMessage("user1", 2, "messageId2222", LocalDate.now());
        messageStorage.addMessage("user2", 1, "messageId3333", LocalDate.now());

        System.out.println(messageStorage.getMessageId("user1", 1)); // Output: messageId1
        System.out.println(messageStorage.getMessageId("user2", 1)); // Output: messageId3
        System.out.println(messageStorage.getMessageId("user1", 2)); // Output: messageId2
        System.out.println(messageStorage.getMessageId("user1", 3)); // Output: null
    }*/
}
