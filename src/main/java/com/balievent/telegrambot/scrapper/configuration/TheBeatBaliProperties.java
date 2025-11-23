package com.balievent.telegrambot.scrapper.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.balievent.client.thebeatbali")
@Data
public class TheBeatBaliProperties {

    private String baseUrl;
}

