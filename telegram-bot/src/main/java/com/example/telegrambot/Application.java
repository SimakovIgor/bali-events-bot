package com.example.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    //todo: Оставить обработку только одного формата: dd.mm.yyyy
    //todo: Перевести все сообщения на английский
    //todo: возвращать карту к каждому событию
    //Иначе выдавать ошибку
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
