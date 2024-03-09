package com.balievent.telegrambot.service.handler.callback.monthpagination;

import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.service.service.UserDataService;
import com.balievent.telegrambot.util.DateUtil;
import com.balievent.telegrambot.util.KeyboardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Service
public abstract class AbstractMonthPaginationHandler extends ButtonCallbackHandler {
    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected EventService eventService;

    protected abstract UserData updateUserData(Update update);

    @Override
    @Transactional
    public void handle(final Update update) throws TelegramApiException {
        final LocalDate calendarDate = updateUserData(update).getSearchEventDate();
        final String formattedMonth = DateUtil.getFormattedMonth(calendarDate);
        final String detailedEventsForMonth = eventService.getMessageWithEventsGroupedByDayFull(calendarDate, 1, calendarDate.lengthOfMonth());
        final String eventListMessage = TgBotConstants.EVENT_LIST_TEMPLATE.formatted(formattedMonth, detailedEventsForMonth);

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(eventListMessage)
            .replyMarkup(KeyboardUtil.createMonthInlineKeyboard(calendarDate))
            .build();

        myTelegramBot.execute(editMessageText);
    }

}
