package com.balievent.telegrambot.service.callback;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.MyTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
public abstract class ButtonCallbackHandler {
    @Autowired
    protected MyTelegramBot myTelegramBot;

    public abstract CallbackHandlerType getCallbackHandlerType();

    public abstract void handle(Update update) throws TelegramApiException;

    protected void removeMediaMessage(final Long chatId, final UserData userData) {
        if (CollectionUtils.isEmpty(userData.getMediaMessageIdList())) {
            return;
        }
        try {
            myTelegramBot.execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(userData.getMediaMessageIdList())
                .build());

        } catch (TelegramApiException e) {
            log.error("Media message not found {}", e.getMessage());
        }
    }

}
