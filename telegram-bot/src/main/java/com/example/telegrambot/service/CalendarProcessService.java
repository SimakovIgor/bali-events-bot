package com.example.telegrambot.service;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.model.entity.Event;
import com.example.telegrambot.repository.EventRepository;
import com.example.telegrambot.util.CommonUtil;
import com.example.telegrambot.util.GetGoogleMapLink;
import com.example.telegrambot.util.KeyboardUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarProcessService {
    private final EventRepository eventRepository;

    public SendMessage processShort(final Update update, final LocalDate localDate) {
        final int day = localDate.getDayOfMonth();
        final int month = localDate.getMonthValue();
        final int year = localDate.getYear();

        final String eventListToday = findEventListToday(day, month, year);

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(String.format("%s %02d.%02d.%d%n%s", MyConstants.LIST_OF_EVENTS_ON, day, month, year, eventListToday)) // текст сообщения
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear())) // клавиатура
            .build();
    }

    public EditMessageText processDig(final Update update, final MessageStorage messageStorage) {

        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final String callbackData = callbackQuery.getData();

        // Получение идентификатора сообщения из колбэк-данных Пример SHOW_MORE:123
        final String chatIdString = callbackQuery.getMessage().getChatId().toString();    // ID пользователя чата
        final Integer messageIdFromCallbackData = getMessageIdFromCallbackData(callbackData);
        final Integer messageId = Integer.parseInt(messageStorage.getMessageId(chatIdString, messageIdFromCallbackData)); // ID сообщения
        final LocalDate localDate = messageStorage.getLocalDate(chatIdString, getMessageIdFromCallbackData(callbackData)); // Дата сообщения
        final String newCallbackData = MyConstants.SHOW_LESS + MyConstants.SHOW_SEPARATOR + messageIdFromCallbackData;

        final int day = localDate.getDayOfMonth();
        final int month = localDate.getMonthValue();
        final int year = localDate.getYear();

        final String eventListToday = findListToday(day, month, year);
        final Long chatId = callbackQuery.getMessage().getChatId();

        return EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(String.format("%s %02d.%02d.%d%n%s", MyConstants.LIST_OF_EVENTS_ON, day, month, year, eventListToday)) // текст сообщения
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.updateButton(newCallbackData))   // Изменение названия и содержание кнопки
            .build();
    }

    public EditMessageText processFew(final Update update, final MessageStorage messageStorage) {

        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final String callbackData = callbackQuery.getData();

        // Получение идентификатора сообщения из колбэк-данных Пример SHOW_MORE:123
        final String chatIdString = callbackQuery.getMessage().getChatId().toString();    // ID чата
        final Integer messageIdFromCallbackData = getMessageIdFromCallbackData(callbackData);
        final Integer messageId = Integer.parseInt(messageStorage.getMessageId(chatIdString, messageIdFromCallbackData)); // ID сообщения
        final String newCallbackData = MyConstants.SHOW_MORE + MyConstants.SHOW_SEPARATOR + messageIdFromCallbackData;

        final Long chatId = callbackQuery.getMessage().getChatId();

        return EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(MyConstants.LIST_OF_MORE) // текст сообщения
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.restoreButton(newCallbackData))   // Изменение названия и содержание кнопки
            .build();
    }

    private String findEventListToday(final int day, final int month, final int year) {
        final LocalDateTime from = LocalDateTime.of(year, month, day, 0, 0);
        final LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59);

        final List<Event> eventList = eventRepository.findEventsByStartDateBetween(from, end);

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            stringBuilder.append(i + 1).append(". ")
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n");
        }
        return stringBuilder.toString();
    }

    private String findListToday(final int day, final int month, final int year) {
        final LocalDateTime from = LocalDateTime.of(year, month, day, 0, 0);
        final LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59);

        final List<Event> eventList = eventRepository.findEventsByStartDateBetween(from, end);

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            stringBuilder.append(i + 1).append(". ")    // создаем нумерованный список событий на указанный день в виде строки
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n")
                .append("Location:")
                .append("\n")
                .append(event.getLocationAddress())
                .append("\n")
                .append("Coordinates:")
                .append("\n")
                .append(GetGoogleMapLink.getGoogleMapLinkFull(event.getCoordinates(), event.getCoordinates()))
                .append("\n")
                .append("----------------")
                .append("\n\n\n");
        }

        return stringBuilder.toString();
    }

    private Integer getMessageIdFromCallbackData(final String callbackData) {
        final String[] parts = callbackData.split(MyConstants.SHOW_SEPARATOR);
        if (parts.length < 2 || parts[1].isEmpty()) {
            throw new NumberFormatException("Invalid callback data format");
        }
        return Integer.parseInt(parts[1]);
    }
}
