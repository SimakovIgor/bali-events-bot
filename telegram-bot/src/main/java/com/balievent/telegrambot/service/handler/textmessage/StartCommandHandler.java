package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class StartCommandHandler implements TextMessageHandler {

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.START_COMMAND;
    }

    @Override
    public SendMessage handle(final Update update) {
        final Long chatId = update.getMessage().getChatId();

        return SendMessage.builder()
            .chatId(chatId)
            .text(TgBotConstants.GREETING_MESSAGE_TEMPLATE.formatted())
            .replyMarkup(KeyboardUtil.createInlineKeyboard(TelegramButton.LETS_GO))
            .build();
    }

}
