package com.balievent.telegrambot.service.handler.callback.impl.searchprocess;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.Location;
import com.balievent.telegrambot.repository.LocationRepository;
import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.service.EventSearchCriteriaService;
import com.balievent.telegrambot.util.KeyboardUtil;
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

        eventSearchCriteriaService.updateSearchCriteria(chatId, selectedDate);

        final List<String> locationIds = locationRepository.findAll()
            .stream()
            .map(Location::getId)
            .toList();

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(TgBotConstants.EVENT_TYPE_QUESTION)
            .replyMarkup(KeyboardUtil.createEventLocationsSelectionKeyboard(locationIds))
            .build();

        myTelegramBot.execute(editMessageText);
    }
}
