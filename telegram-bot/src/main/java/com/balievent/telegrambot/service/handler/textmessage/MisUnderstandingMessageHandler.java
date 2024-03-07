package com.balievent.telegrambot.service.handler.textmessage;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MisUnderstandingMessageHandler implements TextMessageHandler {
    private static final String MIS_UNDERSTANDING_MESSAGE = "🚫 This word(s) is not reserved: %s List of reserved words /start";

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.MIS_UNDERSTANDING_MESSAGE;
    }

    @Override
    public SendMessage handle(final Update update) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MIS_UNDERSTANDING_MESSAGE.formatted(update.getMessage().getText()))
            .build();
    }
}
