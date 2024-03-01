package com.balievent.telegrambot.service.handler.callback.pagination;

import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.contant.Settings;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.CallbackHandlerMessageType;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class PreviousPaginationHandler extends AbstractPaginationHandler {

    @Override
    public CallbackHandlerMessageType getHandlerType() {
        return CallbackHandlerMessageType.PREVIOUS_PAGINATION;
    }

    @Override
    public EditMessageText handle(final Update update) {
        final Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        final UserData userData = userDataStorage.decrementPageAndGetUserData(callbackChatId);
        final String eventListToday = getBriefEventsForToday(userData);

        return EditMessageText.builder()
            .chatId(callbackChatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(String.format("%s %s %n%n %s", MyConstants.LIST_OF_EVENTS_ON,
                userData.getCalendarDate().format(Settings.PRINT_DATE_TIME_FORMATTER), eventListToday))
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.getPaginationKeyboard())
            .build();
    }
}
