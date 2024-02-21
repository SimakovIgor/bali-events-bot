package com.example.telegrambot.service;

import com.example.telegrambot.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageProcessService {

    private final EventRepository eventRepository;

    public List<SendMediaGroup> process(final Update update, final LocalDate eventDate) {
        final List<InputMediaPhoto> eventPhotos = findEventPhotos(eventDate);
        final List<SendMediaGroup> sendMediaGroups = new ArrayList<>();

        // Группируем фотографии по 8 элементов в SendMediaGroup,
        // т.к. метод sendMediaGroup позволяет отправить от 2 до 8 фотографий за раз
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

    private List<InputMediaPhoto> findEventPhotos(final LocalDate eventDate) {
        final int year = eventDate.getYear();
        final int month = eventDate.getMonthValue();
        final int dayOfMonth = eventDate.getDayOfMonth();

        final LocalDateTime from = LocalDateTime.of(year, month, dayOfMonth, 0, 0);
        final LocalDateTime end = LocalDateTime.of(year, month, dayOfMonth, 23, 59);

        return eventRepository.findEventsByStartDateBetween(from, end)
            .stream()
            .map(event -> {
                final InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
                inputMediaPhoto.setMedia(event.getImageUrl());
                return inputMediaPhoto;
            })
            .toList();
    }
}
