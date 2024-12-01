package com.balievent.telegrambot.bot.configuration;

import com.balievent.telegrambot.bot.constant.CallbackHandlerType;
import com.balievent.telegrambot.bot.constant.TextMessageHandlerType;
import com.balievent.telegrambot.bot.service.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.bot.service.textmessage.TextMessageHandler;
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
    public Map<CallbackHandlerType, ButtonCallbackHandler> callbackHandlers(final List<ButtonCallbackHandler> buttonCallbackHandlerList) {
        return buttonCallbackHandlerList.stream()
            .collect(Collectors.toMap(ButtonCallbackHandler::getCallbackHandlerType, handler -> handler));
    }

}
