package com.balievent.telegrambot.service.handler.callback.eventspagination;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.model.entity.UserData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class NextPageEventsPaginationHandler extends AbstractEventsPaginationHandler {

    @Override
    public TelegramButton getTelegramButton() {
        return TelegramButton.NEXT_EVENTS_PAGE;
    }

    @Override
    protected UserData updateUserData(final Update update) {
        final Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        return userDataService.incrementCurrentPage(callbackChatId);
    }

}
