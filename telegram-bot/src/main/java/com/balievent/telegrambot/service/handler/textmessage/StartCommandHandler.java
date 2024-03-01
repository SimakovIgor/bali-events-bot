package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.service.storage.UserDataStorage;
import com.balievent.telegrambot.service.support.EventService;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StartCommandHandler implements TextMessageHandler {
    private final UserDataStorage userDataStorage;
    private final EventService eventService;

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.START_COMMAND;
    }

    @Override
    public SendMessage handle(final Update update) {
        final LocalDate localDate = userDataStorage.reset(update);
        final String messageWithEventsGroupedByDay = eventService.getMessageWithEventsGroupedByDay(localDate, 1, localDate.lengthOfMonth());

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(MyConstants.HELLO_I_AM_A_BOT_THAT_WILL_HELP_YOU_FIND_EVENTS_IN_BALI + "\n\n" + messageWithEventsGroupedByDay)
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear()))
            .build();
    }
}
