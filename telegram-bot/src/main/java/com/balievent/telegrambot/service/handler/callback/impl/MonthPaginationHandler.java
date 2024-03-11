package com.balievent.telegrambot.service.handler.callback.impl;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.exceptions.ErrorCode;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.service.service.UserDataService;
import com.balievent.telegrambot.util.DateUtil;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MonthPaginationHandler extends ButtonCallbackHandler {

    private final UserDataService userDataService;
    private final EventService eventService;

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.MONTH_PAGINATION;
    }

    private UserData updateUserData(final Update update) {
        final String callbackData = update.getCallbackQuery().getData();
        final Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        if (TelegramButton.NEXT_MONTH_PAGE.getCallbackData().equals(callbackData)) {
            return userDataService.addMonthAndGetUserData(callbackChatId);
        } else if (TelegramButton.PREVIOUS_MONTH_PAGE.getCallbackData().equals(callbackData)) {
            return userDataService.substractMonthAndGetUserData(callbackChatId);
        }
        throw new ServiceException(ErrorCode.ERR_CODE_999);
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final LocalDate calendarDate = updateUserData(update).getSearchEventDate();
        final String formattedMonth = DateUtil.getFormattedMonth(calendarDate);
        final String detailedEventsForMonth = eventService.getMessageWithEventsGroupedByDayFull(calendarDate, 1, calendarDate.lengthOfMonth());
        final String eventListMessage = TgBotConstants.EVENT_LIST_TEMPLATE.formatted(formattedMonth, detailedEventsForMonth);

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(eventListMessage)
            .replyMarkup(KeyboardUtil.createMonthInlineKeyboard(calendarDate))
            .build();

        myTelegramBot.execute(editMessageText);
    }

}
