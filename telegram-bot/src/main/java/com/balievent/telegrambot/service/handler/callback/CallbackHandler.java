package com.balievent.telegrambot.service.handler.callback;

import com.balievent.telegrambot.constant.TelegramButton;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
    TelegramButton getTelegramButton();

    EditMessageText handle(Update update);
}
