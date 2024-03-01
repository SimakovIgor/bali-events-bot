package com.balievent.telegrambot.service.handler.common;

import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandlerType;
import com.balievent.telegrambot.service.storage.MessageDataStorage;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
@RequiredArgsConstructor
public class SendShowMoreMessageHandler implements TextMessageHandler {
    private final MessageDataStorage messageDataStorage;

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.SEND_SHOW_MORE_MESSAGE;
    }

    /**
     * Создание кнопки под сообщением на текущую дату.
     *
     * @param update - событие из телеграмма
     * @return SendMessage - класс для отправки сообщения в телеграмм
     */
    @Override
    public SendMessage handle(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        final Long nextMessageNumber = messageDataStorage.calculateNextMessageId(chatId);
        final InlineKeyboardMarkup replyMarkup = KeyboardUtil.setShowMoreButtonKeyboard(nextMessageNumber, MyConstants.SHOW_FULL_MONTH);
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MyConstants.LIST_OF_MORE)
            .replyMarkup(replyMarkup)
            .build();
    }
}
