package com.example.telegrambot.service.handler.textmessage;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.service.storage.CalendarDataStorage;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StartCommandHandler implements TextMessageHandler {
    private final CalendarDataStorage calendarDataStorage;

    @Override
    public SendMessage handleTextMessage(final Update update) {
        final LocalDate localDate = calendarDataStorage.put(update);
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MyConstants.HELLO_I_AM_A_BOT_THAT_WILL_HELP_YOU_FIND_EVENTS_IN_BALI)
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear()))
            .build();
    }
}
