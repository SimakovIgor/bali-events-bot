package com.balievent.telegrambot.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ERR_CODE_001("ERR.CODE.001", "Пожалуйста, введите команду /start для начала работы с ботом"),
    ERR_CODE_999("ERR.CODE.999", "Произошла ошибка в работе бота. Пожалуйста, попробуйте позже");

    private final String code;
    private final String description;

    public String formatDescription(final Object... args) {
        return String.format(description, args);
    }
}
