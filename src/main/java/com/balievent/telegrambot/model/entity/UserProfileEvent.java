package com.balievent.telegrambot.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
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

@Entity
@Table(name = "userprofile_event")
public class UserProfileEvent {
    @EmbeddedId
    private UserProfileEventKey userProfileEventKey;

    @Column(name = "is_viewed", nullable = false)
    private Boolean isViewed;

    @ManyToOne
    @MapsId("userProfileId")
    @JoinColumn(name = "user_data_id")
    private UserProfile userProfile;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfileEvent that)) {
            return false;
        }

        return Objects.equals(userProfileEventKey, that.userProfileEventKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userProfileEventKey);
    }
}
