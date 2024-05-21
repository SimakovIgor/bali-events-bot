/**
 * Создал Андрей Антонов 4/11/2024 12:06 PM.
 **/

package com.balievent.telegrambot.service.callback.impl;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.Location;
import com.balievent.telegrambot.repository.LocationRepository;
import com.balievent.telegrambot.service.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.service.EventSearchCriteriaService;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StartFilterHandler extends ButtonCallbackHandler {

    private final EventSearchCriteriaService eventSearchCriteriaService;
    private final LocationRepository locationRepository;

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.EVENT_START_FILTER;
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();

        final List<String> locationIdList = locationRepository.findAll()
            .stream()
            .map(Location::getId)
            .toList();

        final List<String> locationNameList = new ArrayList<>(locationIdList);
        locationNameList.add(TelegramButton.DESELECT_ALL_LOCATIONS.getCallbackData());

        eventSearchCriteriaService.saveOrUpdateEventSearchCriteria(chatId, locationNameList);

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(TgBotConstants.EVENT_DATE_QUESTION.formatted())
            .replyMarkup(KeyboardUtil.createEventDateSelectionKeyboard())
            .build();

        myTelegramBot.execute(editMessageText);
    }
}
