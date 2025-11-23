package com.balievent.telegrambot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Embeddable
public class UserProfileEventKey {
    @Column(name = "userprofile_id")
    private Long userProfileId;

    @Column(name = "event_id")
    private Long eventId;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfileEventKey that)) {
            return false;
        }

        return Objects.equals(userProfileId, that.userProfileId)
            && Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(userProfileId);
        result = 31 * result + Objects.hashCode(eventId);
        return result;
    }
}
