package com.balievent.telegrambot.service.handler.callback.impl;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.constant.TgBotConstants;
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
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DayEventsHandler extends ButtonCallbackHandler {
    private final MediaHandler mediaHandler;
    private final UserDataService userDataService;
    private final EventService eventService;

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.DAY_EVENT_PAGE;
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();

        final UserData userData = userDataService.getUserData(chatId);
        final LocalDate eventsDateFor = userData.getSearchEventDate();

        final int currentPage = 1; //Всегда начинаем с первой страницы
        final List<Event> eventList = eventService.findEvents(eventsDateFor, currentPage - 1, Settings.PAGE_SIZE);
        final int eventCount = eventService.countEvents(eventsDateFor);
        final int pageCount = (eventCount + Settings.PAGE_SIZE - 1) / Settings.PAGE_SIZE;

        userDataService.updatePageInfo(chatId, pageCount, currentPage);

        final String displayDate = eventsDateFor.format(Settings.PRINT_DATE_TIME_FORMATTER);
        final BriefDetailedLocationMessageDto detailedLocationMessageDto = MessageBuilderUtil.buildBriefEventsMessage(currentPage, eventList);

        userDataService.saveOrUpdateLocationMap(detailedLocationMessageDto.getLocationMap(), chatId);

        final InlineKeyboardMarkup replyKeyboard = KeyboardUtil.getDayEventsKeyboard(currentPage, pageCount);
        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(TgBotConstants.EVENT_LIST_TEMPLATE.formatted(displayDate, detailedLocationMessageDto.getMessage()))
            .replyMarkup(replyKeyboard)
            .build();

        myTelegramBot.execute(editMessageText);
//        userDataService.updateLastBotMessageId(message.getMessageId(), chatId);
        removeMediaMessage(chatId, userData);
        mediaHandler.handle(chatId, userData);
    }

    private void removeMediaMessage(final Long chatId, final UserData userData) {
        if (CollectionUtils.isEmpty(userData.getMediaMessageIdList())) {
            return;
        }
        try {
            myTelegramBot.execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(userData.getMediaMessageIdList())
                .build());

        } catch (TelegramApiException e) {
            log.error("Media message not found {}", e.getMessage());
        }
    }

}
