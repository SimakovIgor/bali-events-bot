package com.balievent.telegrambot.service.handler.textmessage;

import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.service.storage.CalendarDataStorage;
import com.balievent.telegrambot.service.support.EventService;
import com.balievent.telegrambot.util.CommonUtil;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DateSelectedHandler implements TextMessageHandler {
    private final EventService eventService;
    private final CalendarDataStorage calendarDataStorage;

    @Override
    public TextMessageHandlerType getHandlerType() {
        return TextMessageHandlerType.DATE_SELECTED;
    }

    @Override
    public SendMessage handle(final Update update) {
        final int rowStart = 0;
        final int rowFinish = 9;
        final LocalDate localDate = calendarDataStorage.updateWithSelectedDate(update);
        final int day = localDate.getDayOfMonth();
        final int month = localDate.getMonthValue();
        final int year = localDate.getYear();
        String lineCut = "";

        final String eventListToday = getBriefEventsForToday(day, month, year);

        if (rowStart == 0) {
            final String format = String.format("%s %02d.%02d.%d%n%s", MyConstants.LIST_OF_EVENTS_ON, day, month, year, eventListToday);
            lineCut = getLineIsCutOff(format, 0, rowFinish);
        } else if (rowStart > 0) {
            lineCut = getLineIsCutOff(eventListToday, rowStart, rowFinish);
        }

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(lineCut)
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear()))
            .build();
    }

    private String getBriefEventsForToday(final int day, final int month, final int year) {
        final List<Event> eventList = eventService.findEvents(day, month, year);

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            stringBuilder.append(i + 1).append(". ")
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n");
        }
        return stringBuilder.toString();
    }

    private String getLineIsCutOff(final String formattedMessage, final int startIndex, final int endIndex) {
        final String[] linesArray = formattedMessage.split("\n");
        final List<String> lines = new ArrayList<>();
        Collections.addAll(lines, linesArray);
        //final List<String> lines = Arrays.asList(formattedMessage.split("\n"));
        int newEndIndex = 0;
        if (endIndex > lines.size()) {
            newEndIndex = lines.size();
        } else {
            newEndIndex = endIndex;
        }
        final List<String> events = lines.subList(startIndex, newEndIndex);
        return String.join("\n", events);
    }
}
