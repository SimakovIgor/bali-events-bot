package com.balievent.telegrambot.service.handler.callback.impl;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.EventSearchCriteria;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.service.EventSearchCriteriaService;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.service.service.UserDataService;
import com.balievent.telegrambot.util.KeyboardUtil;
import com.balievent.telegrambot.util.MessageBuilderUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings({
    "PMD.ClassFanOutComplexity",
    "PMD.ExcessiveImports"}
)
public class MonthEventsHandler extends ButtonCallbackHandler {
    public static final LocalTime START_DAY_LOCAL_TIME = LocalTime.of(0, 0);
    public static final LocalTime END_DAY_LOCAL_TIME = LocalTime.of(23, 59, 59);

    private final UserDataService userDataService;
    private final EventSearchCriteriaService eventSearchCriteriaService;
    private final EventService eventService;

    private static LinkPreviewOptions getLinkPreviewOptions(final Event event) {
        final LinkPreviewOptions linkPreviewOptions = new LinkPreviewOptions();
        linkPreviewOptions.setIsDisabled(false);
        linkPreviewOptions.setPreferLargeMedia(true);
        linkPreviewOptions.setUrlField(event.getImageUrl());
        return linkPreviewOptions;
    }

    private List<SendMessage> prepareSendMessageList(final Long chatId) {
        final EventSearchCriteria eventSearchCriteria = eventSearchCriteriaService.getEventSearchCriteria(chatId);
        final TelegramButton dateSearchType = TelegramButton.findByCallbackData(eventSearchCriteria.getDateFilter());
        final Map<LocalDate, List<Event>> eventListByDay = getEventsAndGroupByDay(dateSearchType);

        return eventListByDay.values()
            .stream()
            .flatMap(List::stream)
            .map(event -> SendMessage.builder()
                .chatId(chatId)
                .text(TgBotConstants.EVENT_NAME_TEMPLATE.formatted(MessageBuilderUtil.buildEventsMessage(event)))
                .parseMode(ParseMode.HTML)
                .replyMarkup(KeyboardUtil.getDetailedLocationKeyboard())
                .linkPreviewOptions(getLinkPreviewOptions(event))
                .build())
            .toList();
    }

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.MONTH_EVENTS_PAGE;
    }

    @SneakyThrows
    @Override
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();
        final UserData userData = userDataService.getUserData(chatId);
        removeMediaMessage(chatId, userData);

        final List<SendMessage> sendMessageList = prepareSendMessageList(chatId);

        final Message firstMessage = myTelegramBot.execute(sendMessageList.getFirst());

        for (int i = 1; i < sendMessageList.size() && i < Settings.SHOW_EVENTS_COUNT; i++) {
            final SendMessage sendMessage = sendMessageList.get(i);
            myTelegramBot.execute(sendMessage);

            //todo: refactor this with ScheduledThreadPoolExecutor
            Thread.sleep(500);
        }

        myTelegramBot.execute(SendMessage.builder()
            .chatId(chatId)
            .replyToMessageId(firstMessage.getMessageId())
            .text(TgBotConstants.MORE_OPTIONS_TEMPLATE.formatted(sendMessageList.size()))
            .replyMarkup(KeyboardUtil.getShowMoreOptionsKeyboard(Settings.SHOW_EVENTS_COUNT))
            .build());
    }

    private Map<LocalDate, List<Event>> getEventsAndGroupByDay(final TelegramButton telegramButton) {
        final LocalDate now = LocalDate.now();

        return switch (telegramButton) {
            case SEARCH_TODAY_EVENTS -> {
                final LocalDateTime startLocalDateTime = LocalDateTime.of(now, START_DAY_LOCAL_TIME);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(now, END_DAY_LOCAL_TIME);

                yield eventService.getEventsAndGroupByDay(startLocalDateTime, endLocalDateTime);
            }
            case SEARCH_TOMORROW_EVENTS -> {
                final LocalDate tomorrow = now.plusDays(1);

                final LocalDateTime startLocalDateTime = LocalDateTime.of(tomorrow, START_DAY_LOCAL_TIME);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(tomorrow, END_DAY_LOCAL_TIME);

                yield eventService.getEventsAndGroupByDay(startLocalDateTime, endLocalDateTime);
            }
            case SEARCH_THIS_WEEK_EVENTS -> {
                final LocalDate endLocalDate = now.plusWeeks(1);

                final LocalDateTime startLocalDateTime = LocalDateTime.of(now, START_DAY_LOCAL_TIME);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(endLocalDate, END_DAY_LOCAL_TIME);

                yield eventService.getEventsAndGroupByDay(startLocalDateTime, endLocalDateTime);
            }
            case SEARCH_NEXT_WEEK_EVENTS -> {
                final LocalDate startLocalDate = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                final LocalDate endLocalDate = startLocalDate.plusWeeks(1);

                final LocalDateTime startLocalDateTime = LocalDateTime.of(startLocalDate, START_DAY_LOCAL_TIME);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(endLocalDate, END_DAY_LOCAL_TIME);

                yield eventService.getEventsAndGroupByDay(startLocalDateTime, endLocalDateTime);
            }
            case SEARCH_ON_THIS_WEEKEND_EVENTS -> {
                final LocalDate startLocalDate = now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
                final LocalDate endLocalDate = now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

                final LocalDateTime startLocalDateTime = LocalDateTime.of(startLocalDate, START_DAY_LOCAL_TIME);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(endLocalDate, END_DAY_LOCAL_TIME);

                yield eventService.getEventsAndGroupByDay(startLocalDateTime, endLocalDateTime);
            }
            case SEARCH_SHOW_ALL_EVENTS -> {
                final LocalDateTime startLocalDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0);
                final LocalDateTime endLocalDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.lengthOfMonth(), 0, 0);

                yield eventService.getEventsAndGroupByDay(startLocalDateTime, endLocalDateTime);
            }

            default -> throw new IllegalStateException("Unexpected value: " + telegramButton);
        };

    }

}
