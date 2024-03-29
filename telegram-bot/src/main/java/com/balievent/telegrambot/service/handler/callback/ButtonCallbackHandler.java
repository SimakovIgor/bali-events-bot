package com.balievent.telegrambot.service.handler.callback;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.service.MyTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public abstract class ButtonCallbackHandler {
    @Autowired
    protected MyTelegramBot myTelegramBot;

    public abstract CallbackHandlerType getCallbackHandlerType();

    public abstract void handle(Update update) throws TelegramApiException;
}
