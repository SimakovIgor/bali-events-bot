/**
 * Создал Андрей Антонов 2/13/2024 10:16 AM.
 **/

package com.example.telegrambot.service;

import com.example.telegrambot.contant.Constants;
import com.example.telegrambot.util.DateUtil;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {

    private final CalendarProcessService calendarProcessService;
    private final ImageProcessService imageProcessService;
    private final CalendarStoreService calendarStoreService;

    public MyTelegramBot(final CalendarProcessService calendarProcessService,
                         final ImageProcessService imageProcessService,
                         CalendarStoreService calendarStoreService) {
        super("6781420399:AAHi0vGFUPnh-7wBzC7si7hw1XRQmrNmPzA");

        this.calendarProcessService = calendarProcessService;
        this.imageProcessService = imageProcessService;
        this.calendarStoreService = calendarStoreService;
    }

    @Override
    public String getBotUsername() {
        return "BaliEventsCoordinatebot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            log.info(update.getMessage().getFrom().getFirstName());
            log.info(update.getMessage().getText());

            String text = update.getMessage().getText();

            // создаем глобальный список пользователей в -> private final Map<Long, LocalDate> calendarStore = new ConcurrentHashMap<>(100);
            LocalDate localDate = calendarStoreService.putOrUpdate(update); // Преобразуем строку "15 Jan" или JANUARY (01.2024) в "15.01.2024" и сохраняем
            String newtext =  String.format("%02d.%02d.%d", localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear()); // перезаписываем воходящую строку

            if (text.contains("/start")) {
                execute(getStartMessage(update, calendarStoreService.get(update)));
            } else if (DateUtil.isCorrectDateFormat(newtext)) {
                execute(calendarProcessService.process(update, localDate));
                execute(imageProcessService.process(update, localDate));
            } else {
                execute(getMisUnderstandingMessage(update));
            }

        } catch (TelegramApiException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    //todo: remove?
//    private boolean showMoreButton(Update update) {
//        if (update.hasCallbackQuery()) { // Обработка нажатия кнопки "Show more"
//            CallbackQuery callbackQuery = update.getCallbackQuery();
//            Long chatId = callbackQuery.getMessage().getChatId();
//            String callbackData = callbackQuery.getData();
//            callbackQuery.getInlineMessageId();
//
//            if (callbackData.equals("show_more")) {
//                // Загрузка дополнительной информации с сервера
//                String additionalInfo = loadAdditionalInfo();
//
//                // Обновление сообщения с дополнительной информацией
//                SendMessage additionalMessage = new SendMessage();
//                additionalMessage.setChatId(chatId);
////                additionalMessage.setReplyToMessageId(startMessageId.get());
//
//                additionalMessage.setText(callbackData + "\n\nДополнительная информация: " + additionalInfo);
//                try {
//                    execute(additionalMessage);
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//            }
//            return true;
//        }
//        return false;
//    }

    private SendMessage getStartMessage(Update update, LocalDate localDate) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(Constants.HELLO_I_AM_A_BOT_THAT_WILL_HELP_YOU_FIND_EVENTS_IN_BALI  )
            .replyMarkup(KeyboardUtil.getKeyboard(localDate.getMonthValue(), localDate.getYear()))
            .build();
    }

    private SendMessage getMisUnderstandingMessage(Update update) {
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(Constants.THIS_WORD_IS_NOT_RESERVED + update.getMessage().getText() + Constants.LIST_OF_RESERVED_WORDS_HELP)
            .build();
    }

}
