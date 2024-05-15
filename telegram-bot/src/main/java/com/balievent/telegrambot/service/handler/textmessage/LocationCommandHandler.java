/**
 * Создал Андрей Антонов 4/19/2024 1:43 AM.
 **/

package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.constant.TextMessageHandlerType;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.util.KeyboardUtil;
import com.balievent.telegrambot.util.MessageBuilderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
//todo: избавиться при переходе на кнопки в detailed location
public class LocationCommandHandler extends TextMessageHandler {
    private final EventService eventService;

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.LOCATION_COMMAND;
    }

    @Override
    //todo: избавиться при переходе на кнопки в detailed location
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getMessage().getChatId();
        final String textMessage = update.getMessage().getText();

        final UserData userData = userDataService.getUserData(chatId);
        userDataService.saveUserMessageId(update.getMessage().getMessageId(), chatId);

        clearChat(chatId, userData);

        final LocalDate searchEventDate = userData.getSearchEventDate();
        final String displayDate = searchEventDate.format(Settings.PRINT_DATE_TIME_FORMATTER);

        final Long locationId = userData.getEventMap().get(textMessage);
        final Event event = eventService.findEventsById(locationId);
        final String eventsBriefMessage = MessageBuilderUtil.buildEventsMessage(List.of(event));

        final String title = textMessage.replace("/", "")
            .replace("_", " "); //?

        final SendMessage sendMessage = SendMessage.builder()
            .chatId(chatId)
            .text(TgBotConstants.EVENT_NAME_TEMPLATE.formatted(title, displayDate, eventsBriefMessage))
            .parseMode(ParseMode.HTML)
            .replyMarkup(KeyboardUtil.getDetailedLocationKeyboard())
            .linkPreviewOptions(getLinkPreviewOptions(event))
            .build();

        final Message message = myTelegramBot.execute(sendMessage);
        userDataService.updateLastBotMessageId(message.getMessageId(), chatId);
    }

    private LinkPreviewOptions getLinkPreviewOptions(final Event event) {
        final LinkPreviewOptions linkPreviewOptions = new LinkPreviewOptions();
        linkPreviewOptions.setIsDisabled(false);
        linkPreviewOptions.setPreferLargeMedia(true);
        linkPreviewOptions.setUrlField(event.getImageUrl());
        return linkPreviewOptions;
    }

}
