package com.example.telegrambot.service.handler.textmessage;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.service.storage.MessageDataStorage;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
@RequiredArgsConstructor
public class SendShowMoreMessageHandler {
    private final MessageDataStorage messageDataStorage;

    /**
     * Создание кнопки под сообщением на текущую дату.
     *
     * @param update - событие из телеграмма
     * @return SendMessage - класс для отправки сообщения в телеграмм
     */
    public SendMessage handleTextMessage(final Update update, final String callbackName) {
        final String chatId = update.getMessage().getChatId().toString();
        final Long nextMessageNumber = messageDataStorage.calculateNextMessageId(chatId);
        final InlineKeyboardMarkup replyMarkup = KeyboardUtil.setShowMoreButtonKeyboard(nextMessageNumber, callbackName);
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MyConstants.LIST_OF_MORE)
            .replyMarkup(replyMarkup)
            .build();
    }
}
