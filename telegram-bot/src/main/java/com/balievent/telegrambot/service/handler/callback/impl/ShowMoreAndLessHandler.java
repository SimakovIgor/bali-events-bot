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
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ShowMoreAndLessHandler extends ButtonCallbackHandler {
    private final EventService eventService;
    private final UserDataService userDataService;

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.SHOW_MORE_OR_LESS_EVENTS;
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final CallbackQuery callbackQuery = update.getCallbackQuery();

        final String text = getText(update);
        final InlineKeyboardMarkup replyMarkup = replyMarkup(update);

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(update.getCallbackQuery().getMessage().getChatId())
            .messageId(callbackQuery.getMessage().getMessageId())
            .text(text)
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(replyMarkup)
            .build();

        myTelegramBot.execute(editMessageText);
    }

    private String getText(final Update update) {
        final String callbackData = update.getCallbackQuery().getData();

        final UserData userData = userDataService.getUserData(update.getCallbackQuery().getMessage().getChatId());
        final LocalDate calendarDate = userData.getSearchEventDate();

        final String formattedMonth = DateUtil.getFormattedMonth(calendarDate);
        final String message = getDetailedMessage(callbackData, calendarDate);
        return TgBotConstants.EVENT_LIST_TEMPLATE.formatted(formattedMonth, message);
    }

    private String getDetailedMessage(final String callbackData, final LocalDate calendarDate) {
        if (TelegramButton.SHOW_MONTH_FULL.getCallbackData().equals(callbackData)) {
            return eventService.getMessageWithEventsGroupedByDayFull(calendarDate, 1, calendarDate.lengthOfMonth());
        } else if (TelegramButton.SHOW_MONTH_LESS.getCallbackData().equals(callbackData)) {
            return eventService.getMessageWithEventsGroupedByDay(calendarDate, 1, calendarDate.lengthOfMonth());
        }
        throw new ServiceException(ErrorCode.ERR_CODE_999);
    }

    private InlineKeyboardMarkup replyMarkup(final Update update) {
        final String callbackData = update.getCallbackQuery().getData();
        if (TelegramButton.SHOW_MONTH_FULL.getCallbackData().equals(callbackData)) {
            return KeyboardUtil.createInlineKeyboard(TelegramButton.SHOW_MONTH_LESS);
        } else if (TelegramButton.SHOW_MONTH_LESS.getCallbackData().equals(callbackData)) {
            return KeyboardUtil.createInlineKeyboard(TelegramButton.SHOW_MONTH_FULL);
        }
        throw new ServiceException(ErrorCode.ERR_CODE_999);
    }
}
