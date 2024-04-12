package com.balievent.telegrambot.service.handler.callback.impl.filterprocess;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.EventSearchCriteria;
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
public class EventLocationsQuestionHandler extends ButtonCallbackHandler {
    private final EventSearchCriteriaService eventSearchCriteriaService;
    private final LocationRepository locationRepository;

    private static boolean isSelectDeselectButtons(final String selectedLocation) {
        return TelegramButton.SELECT_ALL_LOCATIONS.getCallbackData().equals(selectedLocation)
            || TelegramButton.DESELECT_ALL_LOCATIONS.getCallbackData().equals(selectedLocation);
    }

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.EVENT_LOCATIONS_SELECTION;
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();
        final String selectedLocation = update.getCallbackQuery().getData();

        final List<String> locationIds = locationRepository.findAll()// метод полностью дублирует class EventDateQuestionHandler()
            .stream()
            .map(Location::getId)
            .toList();

        // здесь удаляются / добавляются локации
        final EventSearchCriteria eventSearchCriteria;
        if (isSelectDeselectButtons(selectedLocation)) {
            eventSearchCriteria = selectDeselectAll(selectedLocation, locationIds, chatId);
        } else {
            eventSearchCriteria = eventSearchCriteriaService.toggleLocationName(chatId, selectedLocation);
        }

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(TgBotConstants.EVENT_LOCATIONS_QUESTION)
            .replyMarkup(KeyboardUtil.createEventLocationsSelectionKeyboard(locationIds, eventSearchCriteria.getLocationNameList()))
            .build();

        myTelegramBot.execute(editMessageText);
    }

    private EventSearchCriteria selectDeselectAll(final String selectedLocation,
                                                  final List<String> locationIds,
                                                  final Long chatId) {
        // если нужно удалить все локации
        if (TelegramButton.DESELECT_ALL_LOCATIONS.getCallbackData().equals(selectedLocation)) {
            // удалить все локации кроме последней
            return eventSearchCriteriaService.deselectAll(chatId);
        } else {
            // удалить все локации кроме последней
            return eventSearchCriteriaService.selectAll(chatId, locationIds);
        }
    }
}
