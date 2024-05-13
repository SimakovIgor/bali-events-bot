package com.balievent.telegrambot.service.handler.callback.impl;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.EventSearchCriteria;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.service.EventSearchCriteriaService;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.service.service.UserDataService;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@RequiredArgsConstructor
@Service
@Slf4j
public class MonthEventsHandler extends ButtonCallbackHandler {
    public static final LocalTime START_DAY_LOCAL_TIME = LocalTime.of(0, 0);
    public static final LocalTime END_DAY_LOCAL_TIME = LocalTime.of(23, 59, 59);

    private final UserDataService userDataService;
    private final EventSearchCriteriaService eventSearchCriteriaService;
    private final EventService eventService;

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.MONTH_EVENTS_PAGE;
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();
        final UserData userData = userDataService.getUserData(chatId);

        //todo: Тут теперь не хардкодная дата
        final LocalDate calendarDate = userData.getSearchEventDate();
        // Возвращает строковое представление месяца в заданной календарной дате.
        final EventSearchCriteria eventSearchCriteria = eventSearchCriteriaService.getEventSearchCriteria(chatId);
        final TelegramButton telegramButton = TelegramButton.findByCallbackData(eventSearchCriteria.getDateFilter());

        final String detailedEventsForMonth = getMessageWithEventsGroupedByDayFull(telegramButton);

        // Здесь формируется строки /01_04_2024 : 8 events -> в какую дату сколько сообщений добавляем перевод строки
        final String eventListMessage = TgBotConstants.EVENT_LIST_TEMPLATE.formatted(telegramButton.getCallbackData(), detailedEventsForMonth);

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(eventListMessage)
            .replyMarkup(KeyboardUtil.createMonthInlineKeyboard(calendarDate))
            .build();

        removeMediaMessage(chatId, userData);
        myTelegramBot.execute(editMessageText);
    }

    private String getMessageWithEventsGroupedByDayFull(final TelegramButton telegramButton) {
        final LocalDate now = LocalDate.now();

        return switch (telegramButton) {
            case SEARCH_TODAY_EVENTS -> {
                final LocalDateTime startLocalDateTime = LocalDateTime.of(now, START_DAY_LOCAL_TIME);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(now, END_DAY_LOCAL_TIME);

                yield eventService.getMessageWithEventsGroupedByDay(startLocalDateTime, endLocalDateTime);
            }
            case SEARCH_TOMORROW_EVENTS -> {
                final LocalDate tomorrow = now.plusDays(1);

                final LocalDateTime startLocalDateTime = LocalDateTime.of(tomorrow, START_DAY_LOCAL_TIME);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(tomorrow, END_DAY_LOCAL_TIME);

                yield eventService.getMessageWithEventsGroupedByDay(startLocalDateTime, endLocalDateTime);
            }
            case SEARCH_THIS_WEEK_EVENTS -> {
                final LocalDate endLocalDate = now.plusWeeks(1);

                final LocalDateTime startLocalDateTime = LocalDateTime.of(now, START_DAY_LOCAL_TIME);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(endLocalDate, END_DAY_LOCAL_TIME);

                yield eventService.getMessageWithEventsGroupedByDay(startLocalDateTime, endLocalDateTime);
            }
            case SEARCH_NEXT_WEEK_EVENTS -> {
                final LocalDate startLocalDate = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                final LocalDate endLocalDate = startLocalDate.plusWeeks(1);

                final LocalDateTime startLocalDateTime = LocalDateTime.of(startLocalDate, START_DAY_LOCAL_TIME);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(endLocalDate, END_DAY_LOCAL_TIME);

                yield eventService.getMessageWithEventsGroupedByDay(startLocalDateTime, endLocalDateTime);
            }
            case SEARCH_ON_THIS_WEEKEND_EVENTS -> {
                final LocalDate startLocalDate = now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
                final LocalDate endLocalDate = now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

                final LocalDateTime startLocalDateTime = LocalDateTime.of(startLocalDate, START_DAY_LOCAL_TIME);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(endLocalDate, END_DAY_LOCAL_TIME);

                yield eventService.getMessageWithEventsGroupedByDay(startLocalDateTime, endLocalDateTime);
            }
            case SEARCH_SHOW_ALL_EVENTS -> {
                final LocalDateTime startLocalDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.lengthOfMonth(), 0, 0);

                yield eventService.getMessageWithEventsGroupedByDay(startLocalDateTime, endLocalDateTime);
            }

            default -> throw new IllegalStateException("Unexpected value: " + telegramButton);
        };

    }

}
