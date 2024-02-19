/**
 * Создал Андрей Антонов 2/16/2024 3:53 PM.
 **/

package com.example.telegrambot.service;

import com.example.telegrambot.contant.Constants;
import lombok.experimental.UtilityClass;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@UtilityClass
public class GetGoogleMapLink {

    public static String GetLink(String text, String ling) {
        return "<a href=\"" + ling + "\">" + text + "</a>";
    }

    public static String GetGoogleMapLink(String text, String coordinates) {
        return "<a href=\"" + getGoogleMap(coordinates) + "\">" + text + "</a>";
    }
    public static String GetGoogleMapLinkFull(String text, String coordinates) {
        return "<a href=\"" + getGoogleMapFull(coordinates) + "\">" + text + "</a>";
    }

    public static String getGoogleMap(final String coordinates) {
        String[] parts = coordinates.split(",");
        double latitude = Double.parseDouble(parts[0]);
        double longitude = Double.parseDouble(parts[1]);

        return String.format("https://maps.google.com/maps?q=%f,%f", latitude, longitude);
    }

    public static String getGoogleMapFull(final String coordinates) {
        String[] parts = coordinates.split(",");
        double latitude = Double.parseDouble(parts[0]);
        double longitude = Double.parseDouble(parts[1]);

        String formattedLatitude = formatCoordinate(latitude, "N", "S");
        String formattedLongitude = formatCoordinate(longitude, "E", "W");

        try {
            String encodedLatitude = URLEncoder.encode(formattedLatitude, "UTF-8");
            String encodedLongitude = URLEncoder.encode(formattedLongitude, "UTF-8");

            return String.format(Constants.GOOGLE_MAPS_WITH_COORDINATES, encodedLatitude, encodedLongitude, parts[0], parts[1], parts[0], parts[1]);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Constants.GOOGLE_MAPS;
        }
    }

    public static String formatCoordinate(double coordinate, String positiveDirection, String negativeDirection) {
        String direction = coordinate >= 0 ? positiveDirection : negativeDirection;
        coordinate = Math.abs(coordinate);
        int degrees = (int) coordinate;
        coordinate = (coordinate - degrees) * 60;
        int minutes = (int) coordinate;
        double seconds = (coordinate - minutes) * 60;
        return String.format("%d°%02d'%04.1f\"%s", degrees, minutes, seconds, direction);
    }
}
