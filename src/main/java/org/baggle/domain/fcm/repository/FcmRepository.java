package org.baggle.domain.fcm.repository;

import org.baggle.domain.fcm.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByUserId(Long userId);

    List<FcmToken> findByUserParticipationsMeetingId(Long meetingId);
}