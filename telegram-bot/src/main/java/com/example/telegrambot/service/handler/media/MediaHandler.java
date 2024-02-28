package com.example.telegrambot.service.handler.media;

import com.example.telegrambot.service.support.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaHandler {

    private final EventService eventService;

    private static boolean isOnlyOneGroupWithOneMedia(final List<SendMediaGroup> sendMediaGroups) {
        return sendMediaGroups.size() == 1
            && sendMediaGroups.getFirst().getMedias().size() == 1;
    }

    private static boolean isMultipleGroupsAndLastHaveOnlyOneMedia(final List<SendMediaGroup> sendMediaGroups) {
        return sendMediaGroups.size() > 1
            && sendMediaGroups.getLast().getMedias().size() == 1;
    }

    public List<SendMediaGroup> processMultipleMedia(final Update update, final List<InputMediaPhoto> eventPhotos) {
        final List<SendMediaGroup> sendMediaGroups = splitEventPhotosIntoGroups(update, eventPhotos);

        // Если в последней группы 1 элемент, то перекидываем к нему из предыдущей группы
        if (isMultipleGroupsAndLastHaveOnlyOneMedia(sendMediaGroups)) {
            final SendMediaGroup lastSendMediaGroup = sendMediaGroups.getLast();
            final SendMediaGroup previousSendMediaGroup = sendMediaGroups.get(sendMediaGroups.size() - 2);

            if (lastSendMediaGroup.getMedias().size() == 1) {
                final InputMedia lastMediaInPreviousGroup = previousSendMediaGroup.getMedias().getLast();
                lastSendMediaGroup.getMedias().addFirst(lastMediaInPreviousGroup);
                previousSendMediaGroup.getMedias().removeLast();
            }
        } else if (isOnlyOneGroupWithOneMedia(sendMediaGroups)) {
            sendMediaGroups.removeFirst();
        }

        return sendMediaGroups;
    }

    /**
     * Группируем фотографии по 8 элементов в SendMediaGroup,
     * т.к. метод sendMediaGroup позволяет отправить от 2 до 8 фотографий за раз
     *
     * @param update      - событие из телеграмма
     * @param eventPhotos - список фотографий
     * @return - список SendMediaGroup
     */
    private List<SendMediaGroup> splitEventPhotosIntoGroups(final Update update,
                                                            final List<InputMediaPhoto> eventPhotos) {
        final List<SendMediaGroup> sendMediaGroups = new ArrayList<>();
        for (int startIndex = 0; startIndex < eventPhotos.size(); startIndex += 8) {
            final int endIndex = Math.min(startIndex + 8, eventPhotos.size());
            final List<InputMediaPhoto> subList = eventPhotos.subList(startIndex, endIndex);

            final SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
                .chatId(update.getMessage().getChatId())
                .medias(new ArrayList<>(subList))
                .build();

            sendMediaGroups.add(sendMediaGroup);
        }
        return sendMediaGroups;
    }

    public SendPhoto processSingleMedia(final Update update, final List<InputMediaPhoto> eventPhotos) {
        return SendPhoto.builder()
            .chatId(update.getMessage().getChatId())
            .photo(new InputFile(eventPhotos.getFirst().getMedia()))
            .build();
    }

    public List<InputMediaPhoto> findEventPhotos(final LocalDate eventDate) {
        final int year = eventDate.getYear();
        final int month = eventDate.getMonthValue();
        final int dayOfMonth = eventDate.getDayOfMonth();

        return eventService.findEvents(dayOfMonth, month, year)
            .stream()
            .map(event -> {
                final InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
                inputMediaPhoto.setMedia(event.getImageUrl());
                return inputMediaPhoto;
            })
            .toList();
    }
}
