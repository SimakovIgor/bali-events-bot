package com.balievent.telegrambot.service.handler.callback.showlessmore;

import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.contant.Settings;
import com.balievent.telegrambot.model.entity.Event;
import com.balievent.telegrambot.service.handler.callback.CallbackHandlerMessageType;
import com.balievent.telegrambot.service.support.EventService;
import com.balievent.telegrambot.util.CommonUtil;
import com.balievent.telegrambot.util.GetGoogleMapLink;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowMoreHandler extends AbstractShowHandler {
    private final EventService eventService;

    private static String getShowWord(final String showWord) {
        if (showWord.contains(MyConstants.SHOW_MORE)) {
            return MyConstants.SHOW_LESS;
        } else {
            return MyConstants.SHOW_SHORT_MONTH;
        }
    }

    @Override
    public CallbackHandlerMessageType getHandlerType() {
        return CallbackHandlerMessageType.SHOW_MORE;
    }

    private String getDetailedEventsForToday(final int day, final int month, final int year) {
        final List<Event> eventList = eventService.findEvents(day, month, year);

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            stringBuilder.append(i + 1).append(". ")
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n")
                .append("Location:")
                .append("\n")
                .append(event.getLocationAddress())
                .append("\n")
                .append("Google Maps:")
                .append(GetGoogleMapLink.getGoogleMap(event.getCoordinates(), event.getCoordinates()))
                .append("\n")
                .append("----------------")
                .append("\n\n");
        }

        return stringBuilder.toString();
    }

    @Override
    protected String getText(final Update update) {
        final Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        final String callbackData = update.getCallbackQuery().getData();
        final Long callbackMessageId = getCallbackMessageId(callbackData);
        final LocalDate localDate = messageDataStorage.getLocalDate(callbackChatId, callbackMessageId);

        if (callbackData.contains(MyConstants.SHOW_MORE)) {
            return String.format("%s %s %n %s", MyConstants.LIST_OF_EVENTS_ON, localDate.format(Settings.PRINT_DATE_TIME_FORMATTER),
                getDetailedEventsForToday(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear()));
        } else if (callbackData.contains(MyConstants.SHOW_FULL_MONTH)) {
            return eventService.getMessageWithEventsGroupedByDayFull(localDate, 6, localDate.lengthOfMonth());
        }
        return "";
    }

    @Override
    protected InlineKeyboardMarkup replyMarkup(final Update update) {
        final String callbackData = update.getCallbackQuery().getData();
        final Long callbackMessageId = getCallbackMessageId(callbackData);
        final String newCallbackData = getShowWord(callbackData) + MyConstants.COLON_MARK + callbackMessageId;

        return KeyboardUtil.updateButton(newCallbackData);
    }

}
