package org.baggle.domain.meeting.repository;

import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.meeting.domain.ButtonAuthority;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    @Query("SELECT fcm.user.fcmToken FROM Participation p " +
            "JOIN p.meeting m " +
            "JOIN p.user u " +
            "JOIN u.fcmToken fcm " +
            "WHERE p.buttonAuthority = :buttonAuthority AND m = :meeting")
    List<FcmToken> findFcmTokensByMeetingAndButtonAuthority(Meeting meeting, ButtonAuthority buttonAuthority);

    Participation findFirstByUserIdAndMeetingId(Long userId, Long meetingId);
}
