package com.balievent.telegrambot.service.handler.callback.impl;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.exceptions.ErrorCode;
import com.balievent.telegrambot.exceptions.ServiceException;
import com.balievent.telegrambot.model.dto.BriefDetailedLocationMessageDto;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.handler.common.MediaHandler;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.service.service.UserDataService;
import com.balievent.telegrambot.util.KeyboardUtil;
import com.balievent.telegrambot.util.MessageBuilderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventsPaginationHandler extends ButtonCallbackHandler {

    private final EventService eventService;

    private final UserDataService userDataService;

    private final MediaHandler mediaHandler;

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.EVENTS_PAGINATION;
    }

    @SuppressWarnings("PMD.ReturnCount") //todo: refactor
    private UserData updateUserData(final Update update) {
        final String string = update.getCallbackQuery().getData();
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (TelegramButton.FIRST_EVENTS_PAGE.getCallbackData().equals(string)) {
            return userDataService.updateCurrentPage(chatId, 1);
        } else if (TelegramButton.NEXT_EVENTS_PAGE.getCallbackData().equals(string)) {
            return userDataService.incrementCurrentPage(chatId);
        } else if (TelegramButton.PREVIOUS_EVENTS_PAGE.getCallbackData().equals(string)) {
            return userDataService.decrementCurrentPage(chatId);
        } else if (TelegramButton.LAST_EVENTS_PAGE.getCallbackData().equals(string)) {
            final int pageCount = userDataService.getUserData(chatId).getTotalEventPages();
            return userDataService.updateCurrentPage(chatId, pageCount);
        }
        throw new ServiceException(ErrorCode.ERR_CODE_999);
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final UserData userData = updateUserData(update);

        final List<Event> eventList = eventService.findEvents(userData.getSearchEventDate(),
            userData.getCurrentEventPage() - 1, Settings.PAGE_SIZE);
        final BriefDetailedLocationMessageDto locationMessageDto =
            MessageBuilderUtil.buildBriefEventsMessage(userData.getCurrentEventPage(), eventList);

        userDataService.saveOrUpdateLocationMap(locationMessageDto.getLocationMap(),
            update.getCallbackQuery().getMessage().getChatId());

        final String formattedDate = userData.getSearchEventDate().format(Settings.PRINT_DATE_TIME_FORMATTER);
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(TgBotConstants.EVENT_LIST_TEMPLATE.formatted(formattedDate, locationMessageDto.getMessage()))
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.getDayEventsKeyboard(userData.getCurrentEventPage(), userData.getTotalEventPages()))
            .build();

        myTelegramBot.execute(editMessageText);

        removeMediaMessage(chatId, userData);
        mediaHandler.handle(chatId, userData);
    }

}
