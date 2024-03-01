package com.balievent.telegrambot.service.handler.callback;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
    CallbackHandlerMessageType getHandlerType();

    EditMessageText handle(Update update);
}
