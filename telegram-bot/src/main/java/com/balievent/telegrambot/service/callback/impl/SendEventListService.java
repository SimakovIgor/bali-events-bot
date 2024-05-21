package com.balievent.telegrambot.service.callback.impl;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.Settings;
import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.model.entity.EventSearchCriteria;
import com.balievent.telegrambot.service.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.service.EventSearchCriteriaService;
import com.balievent.telegrambot.service.service.EventService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class SendEventListService extends ButtonCallbackHandler {

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
        final Map<LocalDate, List<Event>> eventListByDay = eventService.getEventListGroupByLocalDate(dateSearchType);

        return eventListByDay.values()
            .stream()
            .flatMap(List::stream)
            .map(event -> SendMessage.builder()
                .chatId(chatId)
                .text(TgBotConstants.EVENT_NAME_TEMPLATE.formatted(MessageBuilderUtil.buildEventsMessage(event)))
                .parseMode(ParseMode.HTML)
                .replyMarkup(KeyboardUtil.getDetailedEventViewKeyboard(event))
                .linkPreviewOptions(getLinkPreviewOptions(event))
                .build())
            .toList();
    }

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.SEND_EVENT_LIST_SERVICE;
    }

    @SneakyThrows
    @Override
    public void handle(final Update update) {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();
        final List<SendMessage> sendMessageList = prepareSendMessageList(chatId);
        final Message firstMessage = myTelegramBot.execute(sendMessageList.getFirst());

        for (int i = 1; i < sendMessageList.size() && i < Settings.SHOW_EVENTS_COUNT; i++) {
            final SendMessage sendMessage = sendMessageList.get(i);
            myTelegramBot.execute(sendMessage);

            //todo: refactor this with ScheduledThreadPoolExecutor
            Thread.sleep(1000);
        }

        myTelegramBot.execute(SendMessage.builder()
            .chatId(chatId)
            .replyToMessageId(firstMessage.getMessageId())
            .text(TgBotConstants.MORE_OPTIONS_TEMPLATE.formatted(sendMessageList.size()))
            .replyMarkup(KeyboardUtil.getShowMoreOptionsKeyboard(Settings.SHOW_EVENTS_COUNT))
            .build());
    }

}
