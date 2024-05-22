package com.balievent.telegrambot.repository;

import com.balievent.telegrambot.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
