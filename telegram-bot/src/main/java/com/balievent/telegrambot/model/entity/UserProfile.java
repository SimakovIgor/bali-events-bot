package com.balievent.telegrambot.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@Getter
@Setter
@ToString

@Entity
@Table(name = "userprofile")
public class UserProfile {
    @Id
    private Long id;

    @Column(name = "last_bot_message_id")
    private Integer lastBotMessageId;

    @Column(name = "last_user_message_id")
    private Integer lastUserMessageId;

}
