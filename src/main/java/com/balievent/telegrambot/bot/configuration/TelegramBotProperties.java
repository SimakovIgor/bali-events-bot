package com.balievent.telegrambot.bot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "com.balievent.bot")
public class TelegramBotProperties {
    private String username;
    private String token;
}
