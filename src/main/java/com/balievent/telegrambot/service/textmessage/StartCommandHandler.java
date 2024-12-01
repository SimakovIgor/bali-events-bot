package com.balievent.telegrambot.service.textmessage;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TextMessageHandlerType;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.UserProfile;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class StartCommandHandler extends TextMessageHandler {

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.START_COMMAND;
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getMessage().getChatId();
        final UserProfile userProfile = userProfileService.saveOrUpdateUserData(chatId);
        userProfileService.saveUserMessageId(update.getMessage().getMessageId(), chatId);

        clearChat(chatId, userProfile);

        final SendMessage sendMessage = SendMessage.builder()
            .chatId(chatId)
            .text(TgBotConstants.GREETING_MESSAGE_TEMPLATE.formatted())
            .replyMarkup(KeyboardUtil.createInlineKeyboard(TelegramButton.LETS_GO))
            .build();

        final Message message = myTelegramBot.execute(sendMessage);
        userProfileService.updateLastBotMessageId(message.getMessageId(), chatId);
    }

}
