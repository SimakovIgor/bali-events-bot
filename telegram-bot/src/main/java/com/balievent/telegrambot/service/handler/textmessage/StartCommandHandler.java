package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.repository.UserDataRepository;
import com.balievent.telegrambot.service.support.EventService;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StartCommandHandler implements TextMessageHandler {
    private static final String HELLO_MESSAGE = """
        👋 Hello!
        I'm a bot that will help you find events in Bali. 🌴
        Write the date in the format: 'dd.mm.yyyy' or choose from the calendar

        %s""";

    private final UserDataRepository userDataRepository;
    private final EventService eventService;

    private static UserData getDefaultUserData(final Long chatId) {
        return UserData.builder()
            .id(chatId)
            .calendarDate(LocalDate.now())
            .currentPage(1)
            .pageCount(1)
            .build();
    }

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.START_COMMAND;
    }

    @Override
    @Transactional
    public SendMessage handle(final Update update) {
        final Long chatId = update.getMessage().getChatId();
        final UserData userData = getDefaultUserData(chatId);

        userDataRepository.save(userData);

        final LocalDate calendarDate = userData.getCalendarDate();
        final String monthEventsMessage = eventService.getMessageWithEventsGroupedByDay(
            calendarDate,
            1,
            calendarDate.lengthOfMonth()
        );

        return SendMessage.builder()
            .chatId(chatId)
            .text(HELLO_MESSAGE.formatted(monthEventsMessage))
            .replyMarkup(KeyboardUtil.setCalendar(calendarDate.getMonthValue(), calendarDate.getYear()))
            .build();
    }
}
