package com.example.telegrambot.service.handler.textmessage;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MisUnderstandingMessageHandler implements TextMessageHandler {

    @Override
    public SendMessage handleTextMessage(final Update update) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(String.format("Это слово(а) не зарезервировано: %s Список зарезервированных слов /help ", update.getMessage().getText()))
            .build();
    }
}
