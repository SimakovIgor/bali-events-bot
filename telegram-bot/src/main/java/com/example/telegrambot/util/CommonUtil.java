/**
 * Создал Андрей Антонов 2/17/2024 11:05 AM.
 **/

package com.example.telegrambot.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtil {

    public static String getLink(final String text, final String url) {
        return "<a href=\"" + url + "\">" + text + "</a>";
    }
}
