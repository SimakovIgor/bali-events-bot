package com.example.telegrambot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MessagePrimaryKey {
    private String chatId;
    private Long messageNumber;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MessagePrimaryKey that = (MessagePrimaryKey) o;
        return Objects.equals(chatId, that.chatId) && Objects.equals(messageNumber, that.messageNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, messageNumber);
    }
}
