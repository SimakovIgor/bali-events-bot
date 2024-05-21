package com.balievent.telegrambot.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtil {

    public static String getLink(final String text, final String url) {
        return "<a href=\"" + url + "\">" + text + "</a>";
    }

    public static String getGoogleMap(final String text, final String coordinates) {
        return "<a href=\"" + getGoogleMap(coordinates) + "\">" + text + "</a>";
    }

    public static String getGoogleMap(final String coordinates) {
        final String[] parts = coordinates.split(",");
        final double latitude = Double.parseDouble(parts[0].replace(",", "."));
        final double longitude = Double.parseDouble(parts[1].replace(",", "."));

        return String.format("https://maps.google.com/maps?q=%f,%f", latitude, longitude);
    }
}
