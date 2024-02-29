package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.contant.MyConstants;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MisUnderstandingMessageHandler implements TextMessageHandler {

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.MIS_UNDERSTANDING_MESSAGE;
    }

    @Override
    public SendMessage handle(final Update update) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(String.format(MyConstants.MIS_UNDERSTANDING_MESSAGE, update.getMessage().getText()))
            .build();
    }
}
