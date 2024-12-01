package com.balievent.telegrambot.exceptions;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String code;

    public ServiceException(final ErrorCode errorCode, final Object... args) {
        super(errorCode.formatDescription(args));
        this.code = errorCode.getCode();
    }

    public ServiceException(final ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.code = errorCode.getCode();
    }
}
