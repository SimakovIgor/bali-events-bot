/**
 * Создал Андрей Антонов 2/13/2024 11:57 AM.
 **/

package com.example.telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
public class TelegramBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi (MyTelegramBot myTelegramBot) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(myTelegramBot);
            return telegramBotsApi;
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            throw new IllegalStateException(e);
        }
     }

    @Bean
    public KeyboardUtil keyboardUtil() {
        return new KeyboardUtil();
    }
}
