package com.balievent.telegrambot.service.handler.callback;

import com.balievent.telegrambot.contant.MyConstants;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
@RequiredArgsConstructor
public class ShowLessHandler extends AbstractShowHandler {

    private static String getShowWord(final String showWord) {
        if (showWord.contains(MyConstants.SHOW_LESS)) {
            return MyConstants.SHOW_MORE;
        } else {
            return MyConstants.SHOW_FULL_MONTH;
        }
    }

    @Override
    protected String getText(final Update update) {
        return MyConstants.LIST_OF_MORE;
    }

    @Override
    protected InlineKeyboardMarkup replyMarkup(final Update update) {
        final String callbackData = update.getCallbackQuery().getData();
        final Long callbackMessageId = getCallbackMessageId(callbackData);
        final String newCallbackData = getShowWord(callbackData) + MyConstants.COLON_MARK + callbackMessageId;

        return KeyboardUtil.restoreButton(newCallbackData);
    }

}
