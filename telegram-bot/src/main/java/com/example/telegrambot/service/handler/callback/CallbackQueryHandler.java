package com.example.telegrambot.service.handler.callback;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackQueryHandler {

    EditMessageText handleCallbackQuery(Update update, String data);
}
