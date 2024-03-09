package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CalendarMonthChangedHandler extends TextMessageHandler {

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.CALENDAR_MONTH_CHANGED;
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getMessage().getChatId();
        final UserData userData = userDataService.updateCalendarDate(update, true);
        userDataService.saveUserMessageId(update.getMessage().getMessageId(), chatId);

        clearChat(chatId, userData);

        final LocalDate localDate = userData.getSearchEventDate();
        final String messageWithEventsGroupedByDay = eventService.getMessageWithEventsGroupedByDay(localDate, 1, localDate.lengthOfMonth());
        final String displayDate = update.getMessage().getText();

        final SendMessage sendMessage = SendMessage.builder()
            .chatId(chatId)
            .text(TgBotConstants.EVENT_LIST_TEMPLATE.formatted(displayDate, messageWithEventsGroupedByDay))
            .replyMarkup(KeyboardUtil.createInlineKeyboard(TelegramButton.SHOW_MONTH_FULL))
            .build();

        final Message message = myTelegramBot.execute(sendMessage);
        userDataService.updateLastBotMessageId(message.getMessageId(), chatId);
    }
}
