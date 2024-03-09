package com.balievent.telegrambot.service.handler.callback.showlessmore;

import com.balievent.telegrambot.service.handler.callback.CallbackHandler;
import com.balievent.telegrambot.service.storage.UserDataService;
import com.balievent.telegrambot.service.support.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public abstract class AbstractShowHandler implements CallbackHandler {
    @Autowired
    protected EventService eventService;
    @Autowired
    protected UserDataService userDataService;

    @Override
    public EditMessageText handle(final Update update) {
        final CallbackQuery callbackQuery = update.getCallbackQuery();

        final String text = getText(update);
        final InlineKeyboardMarkup replyMarkup = replyMarkup(update);

        return EditMessageText.builder()
            .chatId(update.getCallbackQuery().getMessage().getChatId())
            .messageId(callbackQuery.getMessage().getMessageId())
            .text(text)
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(replyMarkup)
            .build();
    }

    protected abstract String getText(Update update);

    protected abstract InlineKeyboardMarkup replyMarkup(Update update);
}
