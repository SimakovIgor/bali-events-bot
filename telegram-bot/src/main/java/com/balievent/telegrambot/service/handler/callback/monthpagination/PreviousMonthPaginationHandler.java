package com.balievent.telegrambot.service.handler.callback.monthpagination;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.model.entity.UserData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class PreviousMonthPaginationHandler extends AbstractMonthPaginationHandler {

    @Override
    public TelegramButton getTelegramButton() {
        return TelegramButton.PREVIOUS_MONTH_PAGE;
    }

    @Override
    protected UserData updateUserData(final Update update) {
        final Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        return userDataService.substractMonthAndGetUserData(callbackChatId);
    }

}
