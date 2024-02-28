package com.example.telegrambot.service.handler.callback;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.service.storage.MessageDataStorage;
import com.example.telegrambot.util.CommonUtil;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class ShowLessHandler implements CallbackQueryHandler {
    private final MessageDataStorage messageDataStorage;

    @Override
    public EditMessageText handleCallbackQuery(final Update update, final String showWord) {
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final String callbackData = callbackQuery.getData();

        // Получение идентификатора сообщения из колбэк-данных Пример SHOW_MORE:123
        final String chatIdString = callbackQuery.getMessage().getChatId().toString();
        final Long messageIdFromCallbackData = CommonUtil.getMessageIdFromCallbackData(callbackData);
        final Integer messageId = Integer.parseInt(messageDataStorage.getMessageTimestamp(chatIdString, messageIdFromCallbackData)); // ID сообщения
        final String newCallbackData = showWord + MyConstants.COLON_MARK + messageIdFromCallbackData;

        final Long chatId = callbackQuery.getMessage().getChatId();

        return EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(MyConstants.LIST_OF_MORE)
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.restoreButton(newCallbackData))
            .build();
    }

}
