package com.balievent.telegrambot.service.handler.callback;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.storage.UserDataService;
import com.balievent.telegrambot.service.support.EventService;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@RequiredArgsConstructor
@Service
@Slf4j
public class MonthEventsButtonCallbackHandler extends ButtonCallbackHandler {
    private final UserDataService userDataService;
    private final EventService eventService;

    @Override
    public TelegramButton getTelegramButton() {
        return TelegramButton.MONTH_EVENTS_PAGE;
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();
        final UserData userData = userDataService.getUserData(chatId);
        final LocalDate calendarDate = userData.getSearchEventDate();
        final String displayDate = calendarDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " (" + calendarDate.getMonthValue() + "." + calendarDate.getYear() + ")";
        final String detailedEventsForMonth = eventService.getMessageWithEventsGroupedByDayFull(calendarDate, 1, calendarDate.lengthOfMonth());
        final String eventListMessage = TgBotConstants.EVENT_LIST_TEMPLATE.formatted(displayDate, detailedEventsForMonth);

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(eventListMessage)
            .replyMarkup(KeyboardUtil.createMonthInlineKeyboard(calendarDate))
            .build();

        removeMediaMessage(chatId, userData);
        myTelegramBot.execute(editMessageText);
    }

    private void removeMediaMessage(final Long chatId, final UserData userData) {
        if (CollectionUtils.isEmpty(userData.getMediaMessageIdList())) {
            return;
        }
        try {
            log.info("Removing media message {}", chatId);
            myTelegramBot.execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(userData.getMediaMessageIdList())
                .build());

        } catch (TelegramApiException e) {
            log.error("Media message not found {}", e.getMessage());
        }
    }
}
