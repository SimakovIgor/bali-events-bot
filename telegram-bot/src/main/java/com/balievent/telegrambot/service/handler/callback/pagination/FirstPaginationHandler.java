package com.balievent.telegrambot.service.handler.callback.pagination;

import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.CallbackHandlerMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class FirstPaginationHandler extends AbstractPaginationHandler {

    @Override
    public CallbackHandlerMessageType getHandlerType() {
        return CallbackHandlerMessageType.FIRST_PAGINATION;
    }

    @Override
    protected UserData updateUserData(final Update update) {
        final Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        return userDataService.setCurrentPage(callbackChatId, 1);
    }

}
