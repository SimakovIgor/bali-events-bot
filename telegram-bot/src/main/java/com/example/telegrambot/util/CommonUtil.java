package com.example.telegrambot.util;

import com.example.telegrambot.contant.MyConstants;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtil {

    public static String getLink(final String text, final String url) {
        return "<a href=\"" + url + "\">" + text + "</a>";
    }

    public static Long getMessageIdFromCallbackData(final String callbackData) {
        final String[] parts = callbackData.split(MyConstants.COLON_MARK);
        if (parts.length < 2 || parts[1].isEmpty()) {
            throw new NumberFormatException("Invalid callback data format");
        }
        return Long.parseLong(parts[1]);
    }
}
