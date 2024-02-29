package com.balievent.telegrambot.service.handler.textmessage;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TextMessageHandler {
    TextMessageHandlerType getHandlerType();

    SendMessage handle(Update update);
}
