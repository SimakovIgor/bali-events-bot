package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.MyTelegramBot;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.service.service.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Service
public abstract class TextMessageHandler {
    @Autowired
    protected MyTelegramBot myTelegramBot;
    @Autowired
    protected EventService eventService;
    @Autowired
    protected UserDataService userDataService;

    public abstract TextMessageHandlerType getHandlerType();

    public abstract void handle(Update update) throws TelegramApiException;

    protected void clearChat(final Long chatId, final UserData userData) {
        final List<Integer> messageIds = userDataService.getAllMessageIdsForDelete(userData);
        if (CollectionUtils.isEmpty(messageIds)) {
            return;
        }
        try {
            myTelegramBot.execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(messageIds)
                .build());
        } catch (TelegramApiException e) {
            log.error("Date selected message not found {}", e.getMessage());
        }
    }
}
