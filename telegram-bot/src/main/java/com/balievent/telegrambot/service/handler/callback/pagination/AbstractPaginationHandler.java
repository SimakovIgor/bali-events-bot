package com.balievent.telegrambot.service.handler.callback.pagination;

import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.CallbackHandler;
import com.balievent.telegrambot.service.storage.UserDataService;
import com.balievent.telegrambot.service.support.EventService;
import com.balievent.telegrambot.service.support.MessageBuilder;
import com.balievent.telegrambot.util.KeyboardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public abstract class AbstractPaginationHandler implements CallbackHandler {
    @Autowired
    protected EventService eventService;
    @Autowired
    protected UserDataService userDataService;

    protected abstract UserData updateUserData(Update update);

    @Override
    @Transactional
    public EditMessageText handle(final Update update) {
        final UserData userData = updateUserData(update);

        final List<Event> eventList = eventService.findEvents(userData.getCalendarDate(), userData.getCurrentPage() - 1, Settings.PAGE_SIZE);
        final String eventsBriefMessage = MessageBuilder.buildBriefEventsMessage(userData.getCurrentPage(), eventList);

        final String formattedDate = userData.getCalendarDate().format(Settings.PRINT_DATE_TIME_FORMATTER);

        return EditMessageText.builder()
            .chatId(update.getCallbackQuery().getMessage().getChatId())
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(TgBotConstants.LIST_OF_EVENTS_ON.formatted(formattedDate, eventsBriefMessage))
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.getPaginationKeyboard(userData.getCurrentPage(), userData.getPageCount()))
            .build();
    }
}
