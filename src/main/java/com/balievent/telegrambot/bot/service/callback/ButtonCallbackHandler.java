package com.balievent.telegrambot.bot.service.callback;

import com.balievent.telegrambot.bot.constant.CallbackHandlerType;
import com.balievent.telegrambot.bot.constant.Settings;
import com.balievent.telegrambot.bot.constant.TgBotConstants;
import com.balievent.telegrambot.bot.service.MyTelegramBot;
import com.balievent.telegrambot.bot.service.service.UserProfileEventService;
import com.balievent.telegrambot.bot.util.KeyboardUtil;
import com.balievent.telegrambot.bot.util.MessageBuilderUtil;
import com.balievent.telegrambot.entity.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
@Slf4j
public abstract class ButtonCallbackHandler {
    @Autowired
    protected MyTelegramBot myTelegramBot;
    @Autowired
    protected UserProfileEventService userProfileEventService;

    private static LinkPreviewOptions getLinkPreviewOptions(final Event event) {
        final LinkPreviewOptions linkPreviewOptions = new LinkPreviewOptions();
        linkPreviewOptions.setIsDisabled(false);
        linkPreviewOptions.setPreferLargeMedia(true);
        linkPreviewOptions.setUrlField(event.getImageUrl());
        return linkPreviewOptions;
    }

    public abstract CallbackHandlerType getCallbackHandlerType();

    public abstract void handle(Update update) throws TelegramApiException;

    private List<SendMessage> prepareSendMessageList(final Long chatId, final List<Event> eventList) {
        return eventList
            .stream()
            .map(event -> SendMessage.builder()
                .chatId(chatId)
                .text(TgBotConstants.EVENT_NAME_TEMPLATE.formatted(MessageBuilderUtil.buildEventsMessage(event)))
                .parseMode(ParseMode.HTML)
                .replyMarkup(KeyboardUtil.getDetailedEventViewKeyboard(event))
                .linkPreviewOptions(getLinkPreviewOptions(event))
                .build())
            .toList();
    }

    //todo: вынести и использовать через композицию, а не через наследование
    protected void sendNextUnseenEvents(final Long chatId)
        throws TelegramApiException, InterruptedException {

        final List<Event> nextUnseenEvents = userProfileEventService.findNextUnseenEvents(chatId);
        final List<SendMessage> sendMessageList = prepareSendMessageList(chatId, nextUnseenEvents);

        final Message firstMessage = myTelegramBot.execute(sendMessageList.getFirst());
        for (int i = 1; i < sendMessageList.size(); i++) {
            final SendMessage sendMessage = sendMessageList.get(i);
            myTelegramBot.execute(sendMessage);

            //todo: refactor this with ScheduledThreadPoolExecutor
            Thread.sleep(1000);
        }

        final int unseenCount = userProfileEventService.findUnseenCount(chatId);

        final int lastUnseenPart = Math.min(unseenCount, Settings.SHOW_EVENTS_COUNT);
        myTelegramBot.execute(SendMessage.builder()
            .chatId(chatId)
            .replyToMessageId(firstMessage.getMessageId())
            .text(TgBotConstants.MORE_OPTIONS_TEMPLATE.formatted(unseenCount))
            .replyMarkup(KeyboardUtil.getShowMoreOptionsKeyboard(lastUnseenPart))
            .build());
    }
}
