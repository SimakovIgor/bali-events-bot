package com.balievent.telegrambot.service.handler.common;

import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.MyTelegramBot;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.service.service.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaHandler {
    private final MyTelegramBot myTelegramBot;
    private final EventService eventService;
    private final UserDataService userDataService;

    public void handle(final Long chatId, final UserData userData) {
        try {
            final List<InputMediaPhoto> eventPhotos = findEventPhotos(userData);
            sendPhotos(chatId, eventPhotos);
        } catch (TelegramApiException e) {
            log.error("Failed to send media", e);
        }
    }

    public void handle(final Long chatId, final Long locationId) {
        try {
            final InputMediaPhoto inputMediaPhoto = getEventsById(locationId);
            sendPhotos(chatId, List.of(inputMediaPhoto));
        } catch (TelegramApiException e) {
            log.error("Failed to send media", e);
        }
    }

    private void sendPhotos(final Long chatId, final List<InputMediaPhoto> eventPhotos)
        throws TelegramApiException {
        if (eventPhotos.isEmpty()) {
            log.info("No event photos found for chatId: {}", chatId);
            return;
        }

        final List<Message> messageList = eventPhotos.size() == 1
                                          ? sendSinglePhoto(chatId, eventPhotos)
                                          : sendMultiplePhotos(chatId, eventPhotos);

        userDataService.updateMediaIdList(messageList, chatId);
    }

    private SendMediaGroup handleMultipleMedia(final Long chatId, final List<InputMediaPhoto> eventPhotos) {
        return SendMediaGroup.builder()
            .chatId(chatId)
            .medias(new ArrayList<>(eventPhotos))
            .build();
    }

    private SendPhoto handleSingleMedia(final Long chatId, final List<InputMediaPhoto> eventPhotos) {
        return SendPhoto.builder()
            .chatId(chatId)
            .photo(new InputFile(eventPhotos.getFirst().getMedia()))
            .build();
    }

    private List<InputMediaPhoto> findEventPhotos(final UserData userData) {
        final int currentPageIndex = userData.getCurrentEventPage() - 1;
        return eventService.findEvents(userData.getSearchEventDate(), currentPageIndex, Settings.PAGE_SIZE)
            .stream()
            .map(event -> {
                final InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
                inputMediaPhoto.setMedia(event.getImageUrl());
                return inputMediaPhoto;
            })
            .toList();
    }

    private InputMediaPhoto getEventsById(final Long eventId) {
        return Optional.of(eventService.findEventsById(eventId))
            .map(event -> {
                final InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
                inputMediaPhoto.setMedia(event.getImageUrl());
                return inputMediaPhoto;
            })
            .orElseThrow(() -> new IllegalStateException("No event found with id: " + eventId));
    }

    private List<Message> sendSinglePhoto(final Long chatId,
                                          final List<InputMediaPhoto> eventPhotos) throws TelegramApiException {
        final Message message = myTelegramBot.execute(handleSingleMedia(chatId, eventPhotos));
        return List.of(message);
    }

    private List<Message> sendMultiplePhotos(final Long chatId,
                                             final List<InputMediaPhoto> eventPhotos) throws TelegramApiException {
        final SendMediaGroup sendMediaGroup = handleMultipleMedia(chatId, eventPhotos);
        return myTelegramBot.execute(sendMediaGroup);
    }

}
