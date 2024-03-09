package com.balievent.telegrambot.service.handler.callback.showlessmore;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.util.DateUtil;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ShowMoreHandlerButton extends AbstractShowHandlerButton {

    @Override
    public TelegramButton getTelegramButton() {
        return TelegramButton.SHOW_MONTH_FULL;
    }

    @Override
    protected String getText(final Update update) {
        final UserData userData = userDataService.getUserData(update.getCallbackQuery().getMessage().getChatId());
        final LocalDate calendarDate = userData.getSearchEventDate();
        final String formattedMonth = DateUtil.getFormattedMonth(calendarDate);
        final String detailedEventsForMonth = eventService.getMessageWithEventsGroupedByDayFull(calendarDate, 1, calendarDate.lengthOfMonth());
        return TgBotConstants.EVENT_LIST_TEMPLATE.formatted(formattedMonth, detailedEventsForMonth);
    }

    @Override
    protected InlineKeyboardMarkup replyMarkup(final Update update) {
        return KeyboardUtil.createInlineKeyboard(TelegramButton.SHOW_MONTH_LESS);
    }

}
