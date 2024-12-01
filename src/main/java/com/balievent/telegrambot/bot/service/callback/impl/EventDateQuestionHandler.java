package com.balievent.telegrambot.bot.service.callback.impl;

import com.balievent.telegrambot.bot.constant.CallbackHandlerType;
import com.balievent.telegrambot.bot.constant.TgBotConstants;
import com.balievent.telegrambot.bot.service.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.bot.service.service.EventSearchCriteriaService;
import com.balievent.telegrambot.bot.util.KeyboardUtil;
import com.balievent.telegrambot.model.entity.EventSearchCriteria;
import com.balievent.telegrambot.model.entity.Location;
import com.balievent.telegrambot.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventDateQuestionHandler extends ButtonCallbackHandler {
    private final EventSearchCriteriaService eventSearchCriteriaService;
    private final LocationRepository locationRepository;

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.EVENT_DATE_SELECTION;
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();
        final String selectedDate = update.getCallbackQuery().getData();

        final List<String> locationIds = locationRepository.findAll()
            .stream()
            .map(Location::getId)
            .toList();

        eventSearchCriteriaService.updateSearchCriteria(chatId, selectedDate);

        final EventSearchCriteria eventSearchCriteria = eventSearchCriteriaService.getEventSearchCriteria(chatId);

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(TgBotConstants.EVENT_LOCATIONS_QUESTION)
            .replyMarkup(KeyboardUtil.createEventLocationsSelectionKeyboard(locationIds, eventSearchCriteria.getLocationNameList()))
            .build();

        myTelegramBot.execute(editMessageText);
    }
}
