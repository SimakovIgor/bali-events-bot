/**
 * Создал Андрей Антонов 4/19/2024 1:43 AM.
 **/

package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.constant.TextMessageHandlerType;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.common.MediaHandler;
import com.balievent.telegrambot.util.KeyboardUtil;
import com.balievent.telegrambot.util.MessageBuilderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
//todo: избавиться при переходе на кнопки в detailed location
public class LocationCommandHandler extends TextMessageHandler {
    private final MediaHandler mediaHandler;

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.LOCATION_COMMAND;
    }

    @Override
    //todo: избавиться при переходе на кнопки в detailed location
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getMessage().getChatId();                                // идентификатор пользователя
        final UserData userData = userDataService.getUserData(chatId);                      // получаем параметры пользователя

        final String textMessage = update.getMessage().getText();                           // текст сообщения например '/2_Amazing_View_Sunset_Party'
        clearChat(chatId, userData);                                                        // удаляем текст локации

        final Long locationId = userData.getLocationMap()
            .get(textMessage);

        mediaHandler.handle(chatId, locationId);                                             // создаем картинку события

        final LocalDate eventsDateFor = userData.getSearchEventDate();                      // дата события
        final String displayDate = eventsDateFor.format(Settings.PRINT_DATE_TIME_FORMATTER); // форматируем дату из 2024-05-01 -> 01.05.2024

        // получаем список возможных локаций
        final Map<String, Long> locationMap = userData.getLocationMap();
        // Ищем ID локации по тексту сообщения от пользователя
        final Long eventId = locationMap.get(textMessage);
        // создаем кнопку навигации из сообщения для возврата к основному меню
        final ReplyKeyboard replyKeyboard = KeyboardUtil.getDetailedLocationKeyboard();
        // список (одна запись) выбранного события
        final List<Event> eventList = eventService.findEventsById(eventId);
        // создаем заголовок сообщения
        final String title = textMessage.replace("/", "").replace("_", " ");
        // создаем текст сообщения
        final String eventsBriefMessage = MessageBuilderUtil.buildEventsMessage(eventList);
        // формируем сообщение для TELEGRAM сервера
        final SendMessage sendMessage = SendMessage.builder()
            .chatId(chatId)
            .text(TgBotConstants.EVENT_NAME_TEMPLATE.formatted(title, displayDate, eventsBriefMessage))
            .parseMode(ParseMode.HTML)
            .replyMarkup(replyKeyboard)
            .disableWebPagePreview(true)
            .build();

        // выводим сообщение пользователю
        final Message message = myTelegramBot.execute(sendMessage);

        userDataService.updateLastBotMessageId(message.getMessageId(), chatId);
    }

}
