package com.balievent.telegrambot.configuration;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandler;
import com.balievent.telegrambot.service.handler.textmessage.TextMessageHandlerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class HandlerConfiguration {
    @Bean(name = "textMessageHandlers")
    public Map<TextMessageHandlerType, TextMessageHandler> textMessageHandlers(final List<TextMessageHandler> textMessageHandlerList) {
        return textMessageHandlerList.stream()
            .collect(Collectors.toMap(TextMessageHandler::getHandlerType, handler -> handler));
    }

    @Bean(name = "callbackHandlers")
    public Map<TelegramButton, ButtonCallbackHandler> callbackHandlers(final List<ButtonCallbackHandler> buttonCallbackHandlerList) {
        return buttonCallbackHandlerList.stream()
            .collect(Collectors.toMap(ButtonCallbackHandler::getTelegramButton, handler -> handler));
    }

}
