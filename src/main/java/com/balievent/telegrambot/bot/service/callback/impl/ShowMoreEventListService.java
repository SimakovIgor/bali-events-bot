package com.balievent.telegrambot.bot.service.callback.impl;

import com.balievent.telegrambot.bot.constant.CallbackHandlerType;
import com.balievent.telegrambot.bot.service.callback.ButtonCallbackHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Service
@Slf4j
public class ShowMoreEventListService extends ButtonCallbackHandler {

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.SHOW_MORE_EVENT_LIST_SERVICE;
    }

    @SneakyThrows
    @Override
    public void handle(final Update update) {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();

        sendNextUnseenEvents(chatId);
    }

}
