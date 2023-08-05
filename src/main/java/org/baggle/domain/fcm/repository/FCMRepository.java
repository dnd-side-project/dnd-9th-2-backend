package org.baggle.domain.fcm.repository;

import org.baggle.domain.fcm.domain.FCMToken;
import org.baggle.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FCMRepository extends JpaRepository<FCMToken, Long> {

    FCMToken findByUser(User user);
    FCMToken findByUserId(Long userId);
    Optional<FCMToken> findByFcmToken(String fcmToken);
}