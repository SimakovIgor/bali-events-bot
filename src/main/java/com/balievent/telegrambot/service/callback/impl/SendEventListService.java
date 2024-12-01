package com.balievent.telegrambot.service.callback.impl;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.EventSearchCriteria;
import com.balievent.telegrambot.service.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.service.EventSearchCriteriaService;
import com.balievent.telegrambot.service.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SendEventListService extends ButtonCallbackHandler {

    private final EventSearchCriteriaService eventSearchCriteriaService;
    private final EventService eventService;

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.SEND_EVENT_LIST_SERVICE;
    }

    @SneakyThrows
    @Override
    public void handle(final Update update) {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();

        userProfileEventService.deleteUserEvents(chatId);

        final EventSearchCriteria eventSearchCriteria = eventSearchCriteriaService.getEventSearchCriteria(chatId);
        final TelegramButton dateSearchType = TelegramButton.findByCallbackData(eventSearchCriteria.getDateFilter());
        final List<Event> eventList = eventService.getFilteredEventList(dateSearchType);
        userProfileEventService.saveUserEvents(eventList, chatId);

        sendNextUnseenEvents(chatId);
    }

}
