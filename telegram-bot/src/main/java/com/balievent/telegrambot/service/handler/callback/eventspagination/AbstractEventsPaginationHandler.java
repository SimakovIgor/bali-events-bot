package com.balievent.telegrambot.service.handler.callback.eventspagination;

import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.handler.common.MediaHandler;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.service.service.UserDataService;
import com.balievent.telegrambot.util.KeyboardUtil;
import com.balievent.telegrambot.util.MessageBuilderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Service
public abstract class AbstractEventsPaginationHandler extends ButtonCallbackHandler {
    @Autowired
    protected EventService eventService;
    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected MediaHandler mediaHandler;

    protected abstract UserData updateUserData(Update update);

    @Override
    @Transactional
    public void handle(final Update update) throws TelegramApiException {
        final UserData userData = updateUserData(update);

        final List<Event> eventList = eventService.findEvents(userData.getSearchEventDate(), userData.getCurrentEventPage() - 1, Settings.PAGE_SIZE);
        final String eventsBriefMessage = MessageBuilderUtil.buildBriefEventsMessage(userData.getCurrentEventPage(), eventList);

        final String formattedDate = userData.getSearchEventDate().format(Settings.PRINT_DATE_TIME_FORMATTER);
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(TgBotConstants.EVENT_LIST_TEMPLATE.formatted(formattedDate, eventsBriefMessage))
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.getDayEventsKeyboard(userData.getCurrentEventPage(), userData.getTotalEventPages()))
            .build();

        myTelegramBot.execute(editMessageText);

        removeMediaMessage(chatId, userData);
        mediaHandler.handle(chatId, userData);
    }

    private void removeMediaMessage(final Long chatId, final UserData userData) {
        if (CollectionUtils.isEmpty(userData.getMediaMessageIdList())) {
            return;
        }
        try {
            myTelegramBot.execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(userData.getMediaMessageIdList())
                .build());

        } catch (TelegramApiException e) {
            log.error("Media message not found {}", e.getMessage());
        }
    }
}
