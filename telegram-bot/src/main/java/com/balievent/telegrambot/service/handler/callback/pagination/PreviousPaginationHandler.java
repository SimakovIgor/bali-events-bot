package com.balievent.telegrambot.service.handler.callback.pagination;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.model.entity.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class PreviousPaginationHandler extends AbstractPaginationHandler {

    @Override
    public TelegramButton getTelegramButton() {
        return TelegramButton.PREVIOUS_EVENTS_PAGE;
    }

    @Override
    protected UserData updateUserData(final Update update) {
        final Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        return userDataService.decrementPageAndGetUserData(callbackChatId);
    }
}
