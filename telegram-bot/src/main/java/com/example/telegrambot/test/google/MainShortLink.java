/**
 * Создал Андрей Антонов 2/17/2024 5:44 AM.
 **/

package com.example.telegrambot.test.google;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainShortLink {

    public static void main(String[] args) {
        String coordinates = "55.752514, 37.623104";
        String[] parts = coordinates.split(",");
        double latitude = Double.parseDouble(parts[0]);
        double longitude = Double.parseDouble(parts[1]);

        String url = String.format("https://maps.google.com/maps?q=%f,%f", latitude, longitude);
        System.out.println(url);

    }
}
