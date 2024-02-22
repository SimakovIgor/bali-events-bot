/**
 * Создал Андрей Антонов 2/22/2024 1:34 PM.
 **/

package com.example.telegrambot.util;

import com.example.telegrambot.contant.MyConstants;
import lombok.experimental.UtilityClass;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@UtilityClass
public class GetGoogleMapLink {

    public static String getLink(final String text, final String ling) {
        return "<a href=\"" + ling + "\">" + text + "</a>";
    }

    public static String getGoogleMap(final String text, final String coordinates) {
        return "<a href=\"" + getGoogleMap(coordinates) + "\">" + text + "</a>";
    }

    public static String getGoogleMapLinkFull(final String text, final String coordinates) {
        return "<a href=\"" + getGoogleMapFull(coordinates) + "\">" + text + "</a>";
    }

    public static String getGoogleMap(final String coordinates) {
        final String[] parts = coordinates.split(",");
        final double latitude = Double.parseDouble(parts[0]);
        final double longitude = Double.parseDouble(parts[1]);

        return String.format("https://maps.google.com/maps?q=%f,%f", latitude, longitude);
    }

    public static String getGoogleMapFull(final String coordinates) {
        final String[] parts = coordinates.split(",");
        final double latitude = Double.parseDouble(parts[0]);
        final double longitude = Double.parseDouble(parts[1]);

        final String formattedLatitude = formatCoordinate(latitude, "N", "S");
        final String formattedLongitude = formatCoordinate(longitude, "E", "W");

        try {
            final String encodedLatitude = URLEncoder.encode(formattedLatitude, "UTF-8");
            final String encodedLongitude = URLEncoder.encode(formattedLongitude, "UTF-8");

            return String.format(MyConstants.GOOGLE_MAPS_WITH_COORDINATES, encodedLatitude, encodedLongitude, parts[0], parts[1], parts[0], parts[1]);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return MyConstants.GOOGLE_MAPS;
        }
    }

    public static String formatCoordinate(final double coordinate, final String positiveDirection, final String negativeDirection) {
        final String direction = coordinate >= 0 ? positiveDirection : negativeDirection;
        final double coordinate1 = Math.abs(coordinate);
        final int degrees = (int) coordinate1;
        final double coordinate2 = (coordinate - degrees) * 60;
        final int minutes = (int) coordinate2;
        final double seconds = (coordinate2 - minutes) * 60;
        return String.format("%dВ°%02d'%04.1f\"%s", degrees, minutes, seconds, direction);
    }
}
