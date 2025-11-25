package com.balievent.telegrambot.repository;

import com.balievent.telegrambot.entity.UserProfileEvent;
import com.balievent.telegrambot.entity.UserProfileEventKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings({"PMD.MethodName", "PMD.MethodNamingConventions"})
public interface UserProfileEventRepository extends JpaRepository<UserProfileEvent, UserProfileEventKey> {

    Page<UserProfileEvent> findByIsViewedAndUserProfileEventKey_UserProfileId(boolean isViewed, Long chatId, Pageable pageable);

    int countByIsViewedAndUserProfileEventKey_UserProfileId(boolean isViewed, Long chatId);

    void deleteByUserProfileEventKey_UserProfileId(Long chatId);
}
