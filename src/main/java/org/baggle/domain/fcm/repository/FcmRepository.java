package org.baggle.domain.fcm.repository;

import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmRepository extends JpaRepository<FcmToken, Long> {

    FcmToken findByUser(User user);

    FcmToken findByUserId(Long userId);

    Optional<FcmToken> findByFcmToken(String fcmToken);

    List<FcmToken> findByUserParticipationsMeetingId(Long meetingId);

}