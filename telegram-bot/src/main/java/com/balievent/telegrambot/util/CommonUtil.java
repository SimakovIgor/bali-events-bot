package com.balievent.telegrambot.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtil {

    public static String getLink(final String text, final String url) {
        return "<a href=\"" + url + "\">" + text + "</a>";
    }

}
