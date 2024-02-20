package com.example.telegrambot.service;

import com.example.telegrambot.repository.EventRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Setter
@Getter
@RequiredArgsConstructor
public class ImageProcessService {

    private final EventRepository eventRepository;

    public SendMediaGroup process(Update update, LocalDate eventDate) {
        List<InputMediaPhoto> eventPhotos = findEventPhotos(eventDate);

        return SendMediaGroup.builder()
            .chatId(update.getMessage().getChatId())
            .medias(new ArrayList<>(eventPhotos))
            .build();
    }

    private List<InputMediaPhoto> findEventPhotos(LocalDate eventDate) {
        int year = eventDate.getYear();
        int month = eventDate.getMonthValue();
        int dayOfMonth = eventDate.getDayOfMonth();

        LocalDateTime from = LocalDateTime.of(year, month, dayOfMonth, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, dayOfMonth, 23, 59);

        return eventRepository.findEventsByStartDateBetween(from, end)
            .stream()
            .map(event -> {
                InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
                inputMediaPhoto.setMedia(event.getImageUrl());
                return inputMediaPhoto;
            })
            .toList();
    }
}
