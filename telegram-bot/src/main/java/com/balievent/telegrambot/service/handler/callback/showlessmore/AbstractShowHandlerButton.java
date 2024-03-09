package com.balievent.telegrambot.service.handler.callback.showlessmore;

import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.service.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public abstract class AbstractShowHandlerButton extends ButtonCallbackHandler {
    @Autowired
    protected EventService eventService;
    @Autowired
    protected UserDataService userDataService;

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final CallbackQuery callbackQuery = update.getCallbackQuery();

        final String text = getText(update);
        final InlineKeyboardMarkup replyMarkup = replyMarkup(update);

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(update.getCallbackQuery().getMessage().getChatId())
            .messageId(callbackQuery.getMessage().getMessageId())
            .text(text)
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(replyMarkup)
            .build();

        myTelegramBot.execute(editMessageText);
    }

    protected abstract String getText(Update update);

    protected abstract InlineKeyboardMarkup replyMarkup(Update update);
}
