package com.balievent.telegrambot.service.handler.callback.showlessmore;

import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.service.handler.callback.CallbackHandler;
import com.balievent.telegrambot.service.storage.MessageDataStorage;
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
    protected MessageDataStorage messageDataStorage;

    protected static Long getCallbackMessageId(final String callbackData) {
        final String[] parts = callbackData.split(MyConstants.COLON_MARK);
        if (parts.length < 2 || parts[1].isEmpty()) {
            throw new NumberFormatException("Invalid callback data format");
        }
        return Long.parseLong(parts[1]);
    }

    @Override
    public EditMessageText handle(final Update update) {
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final String callbackData = callbackQuery.getData();

        final Long callbackMessageId = getCallbackMessageId(callbackData);
        final Long callbackChatId = callbackQuery.getMessage().getChatId();
        final Integer messageId = Integer.parseInt(messageDataStorage.getMessageTimestamp(callbackChatId, callbackMessageId));

        final String text = getText(update);
        final InlineKeyboardMarkup replyMarkup = replyMarkup(update);

        return EditMessageText.builder()
            .chatId(update.getCallbackQuery().getMessage().getChatId())
            .messageId(messageId)
            .text(text)
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(replyMarkup)
            .build();
    }

    protected abstract String getText(Update update);

    protected abstract InlineKeyboardMarkup replyMarkup(Update update);
}
