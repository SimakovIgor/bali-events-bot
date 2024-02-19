/**
 * Создал Андрей Антонов 2/19/2024 3:28 PM.
 **/

package com.example.telegrambot.test.google;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainConvertDateTime {
    public static void main(String[] args) {
        // Пример даты для хранения
        LocalDateTime dateTime = LocalDateTime.now();

        // Конвертация даты в строку
        String dateString = dateTime.format(DateTimeFormatter.ISO_DATE);
        System.out.println("Дата в строковом формате: " + dateString);

        // Конвертация строки обратно в LocalDateTime
        LocalDate parsedDate  = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
        System.out.println("Обратная конвертация: " + parsedDate );
    }
}
