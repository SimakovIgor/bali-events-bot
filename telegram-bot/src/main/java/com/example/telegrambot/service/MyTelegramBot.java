package com.example.telegrambot.service;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.util.DateUtil;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {

    private final CalendarProcessService calendarProcessService;
    private final ImageProcessService imageProcessService;
    private final CalendarStoreService calendarStoreService;
    private final MessageDataStorage messageDataStorage;

    public MyTelegramBot(final CalendarProcessService calendarProcessService,
                         final ImageProcessService imageProcessService,
                         final CalendarStoreService calendarStoreService,
                         final MessageDataStorage messageDataStorage) {
        super("6781420399:AAHi0vGFUPnh-7wBzC7si7hw1XRQmrNmPzA");

        this.calendarProcessService = calendarProcessService;
        this.imageProcessService = imageProcessService;
        this.calendarStoreService = calendarStoreService;
        this.messageDataStorage = messageDataStorage;
    }

    @Override
    public String getBotUsername() {
        return "BaliEventsCoordinatebot";
    }

    /***
     * начало программы для
     *
     * @param update - событие из телеграмма
     */

    @Override
    public void onUpdateReceived(final Update update) {
        try {
            if (update.hasCallbackQuery()) {
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final String callbackData = callbackQuery.getData();
                if (callbackData.contains(MyConstants.SHOW_MORE)) {
                    final EditMessageText editMessageText = calendarProcessService.processShowMore(update, messageDataStorage, MyConstants.SHOW_MORE);
                    execute(editMessageText);
                } else if (callbackData.contains(MyConstants.SHOW_LESS)) {
                    final EditMessageText editMessageText = calendarProcessService.processShowLess(update, messageDataStorage, MyConstants.SHOW_MORE);
                    execute(editMessageText);
                } else if (callbackData.contains(MyConstants.SHOW_FULL_MONTH)) {
                    final EditMessageText editMessageText = calendarProcessService.processShowMore(update, messageDataStorage, MyConstants.SHOW_SHORT_MONTH);
                    execute(editMessageText);
                } else if (callbackData.contains(MyConstants.SHOW_SHORT_MONTH)) {
                    final EditMessageText editMessageText = calendarProcessService.processShowLess(update, messageDataStorage, MyConstants.SHOW_FULL_MONTH);
                    execute(editMessageText);
                }
            } else {
                final String text = update.getMessage().getText();

                if (text.contains("/start")) {
                    calendarStoreService.put(update);
                    execute(getStartMessage(update, calendarStoreService.get(update)));
                    final Message messageExecute = execute(getSignature(update, MyConstants.SHOW_FULL_MONTH));
                    messageDataStorage.addUserMessageData(messageExecute, update, calendarStoreService.get(update));
                } else if (DateUtil.isCalendarMonthChanged(text)) {
                    final LocalDate localDate = calendarStoreService.updateWithCalendarMonthChanged(update);
                    final String listOfDates = calendarProcessService.processCalendarMonthChanged(localDate, 1, 5);
                    execute(getsListOfDates(update, listOfDates, localDate));
                    final Message messageExecute = execute(getSignature(update, MyConstants.SHOW_FULL_MONTH));
                    messageDataStorage.addUserMessageData(messageExecute, update, localDate);
                } else if (DateUtil.isDateSelected(text)) {
                    final LocalDate localDate = calendarStoreService.updateWithSelectedDate(update);
                    execute(calendarProcessService.processShort(update, localDate));
                    executeSendMediaGroup(update, localDate);
                    final Message messageExecute = execute(getSignature(update, MyConstants.SHOW_MORE));
                    messageDataStorage.addUserMessageData(messageExecute, update, localDate);
                } else {
                    execute(getMisUnderstandingMessage(update));
                }
            }
        } catch (TelegramApiException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void executeSendMediaGroup(final Update update, final LocalDate localDate) {
        final List<InputMediaPhoto> eventPhotos = imageProcessService.findEventPhotos(localDate);
        // Если есть только одна фотография, отправляем ее как одиночный медиа-объект
        if (eventPhotos.size() == 1) {
            // Создаем объект запроса для отправки фотографии
            final SendPhoto sendPhoto = new SendPhoto();
            // Указываем chatId - ID чата, куда отправляем фотографию
            sendPhoto.setChatId(update.getMessage().getChatId());
            // Указываем фотографию, которую хотим отправить
            sendPhoto.setPhoto(new InputFile(eventPhotos.get(0).getMedia()));
            try {
                // Выполняем запрос на отправку фотографии
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        } else {
            imageProcessService.process(update, localDate)
                .forEach(sendMediaGroup -> {
                    try {
                        execute(sendMediaGroup);
                    } catch (TelegramApiException e) {
                        throw new IllegalStateException(e.getMessage(), e);
                    }
                });
        }
    }

    private SendMessage getStartMessage(final Update update, final LocalDate localDate) {
        final String listOfDates = MyConstants.HELLO_I_AM_A_BOT_THAT_WILL_HELP_YOU_FIND_EVENTS_IN_BALI + "\n"
             + calendarProcessService.processCalendarMonthChanged(localDate, 1, 5);

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(listOfDates)
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear())) // календарь
            .build();
    }

    private SendMessage getMisUnderstandingMessage(final Update update) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(String.format("Это слово(а) не зарезервировано: %s Список зарезервированных слов /help ", update.getMessage().getText()))
            .build();
    }

    /**
     * Создание кнопки под сообщением на текущую дату.
     *
     * @param update - событие из телеграмма
     *        text - текст сообщения
     *        showMore - метка, которая будет возвращаться при нажатие на кнопку
     * @return SendMessage - класс для отправки сообщения в телеграмм
     */
    private SendMessage getSignature(final Update update, final String callbackName) {
        final String chatId = update.getMessage().getChatId().toString();
        final Long nextMessageNumber = messageDataStorage.calculateNextMessageId(chatId);
        final InlineKeyboardMarkup replyMarkup = KeyboardUtil.setNewButton(nextMessageNumber, callbackName);
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MyConstants.LIST_OF_MORE)
            .replyMarkup(replyMarkup)
            .build();
    }

    private SendMessage getsListOfDates(final Update update, final String textMessage, final LocalDate localDate) {
        final String text = update.getMessage().getText();

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(String.format("%s %s%n%s", MyConstants.LIST_OF_EVENTS_ON, text, textMessage)) // текст сообщения
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear())) // календарь
            .build();
    }
}

