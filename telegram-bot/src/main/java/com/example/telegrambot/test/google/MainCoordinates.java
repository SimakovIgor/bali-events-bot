/**
 * Создал Андрей Антонов 2/16/2024 3:03 PM.
 **/

package com.example.telegrambot.test.google;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainCoordinates {

    public static void main(String[] args) {
        String coordinates = "55.752514, 37.623104";
        String[] parts = coordinates.split(",");
        double latitude = Double.parseDouble(parts[0]);
        double longitude = Double.parseDouble(parts[1]);

        String formattedLatitude = formatCoordinate(latitude, "N", "S");
        String formattedLongitude = formatCoordinate(longitude, "E", "W");

        try {
            String encodedLatitude = URLEncoder.encode(formattedLatitude, "UTF-8");
            String encodedLongitude = URLEncoder.encode(formattedLongitude, "UTF-8");

            String url = String.format("https://www.google.com/maps/place/%s+%s/@%s,%s,17z/data=!4m4!3m3!8m2!3d%s!4d%s?authuser=0&entry=ttu",
                encodedLatitude, encodedLongitude, parts[0], parts[1], parts[0], parts[1]);
            System.out.println(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
