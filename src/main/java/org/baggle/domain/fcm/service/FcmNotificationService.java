package org.baggle.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmNotification;
import org.baggle.domain.fcm.domain.FcmTimer;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.repository.FcmNotificationRepository;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.meeting.domain.ButtonAuthority;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.baggle.global.error.exception.ErrorCode.INVALID_FCM_UPLOAD;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FcmNotificationService {
    private final FcmNotificationRepository fcmNotificationRepository;
    private final FcmTimerRepository fcmTimerRepository;
    private final ParticipationRepository participationRepository;

    public void createFcmNotification(Long key) {
        FcmNotification fcmNotification = FcmNotification.createFcmNotification(key);
        fcmNotificationRepository.save(fcmNotification);
    }

    public List<FcmToken> findFcmTokensByButtonAuthority(Meeting meeting, ButtonAuthority buttonAuthority) {
        return participationRepository.findFcmTokensByMeetingAndButtonAuthority(meeting, buttonAuthority);
    }

    public void createFcmTimer(Long key, LocalDateTime startTime) {
        FcmTimer fcmTimer = FcmTimer.createFcmTimer(key, startTime);
        fcmTimerRepository.save(fcmTimer);
    }
}
