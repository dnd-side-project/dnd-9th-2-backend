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
import org.baggle.domain.fcm.repository.FcmRepository;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.meeting.domain.ButtonAuthority;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmRepository fcmRepository;
    private final FcmNotificationRepository fcmNotificationRepository;
    private final FcmTimerRepository fcmTimerRepository;
    private final ParticipationRepository participationRepository;

    /**
     * TODO: Create 예외 처리
     */
    public void createFcmNotification(Long key) {
        FcmNotification fcmNotification = FcmNotification.builder()
                .id(key)
                .isNotified(Boolean.TRUE)
                .build();
        fcmNotificationRepository.save(fcmNotification);
    }

    /**
     * TODO: 데이터 없을 시 예외 처리
     */
    public void deleteFcmNotification(Long key) {
        fcmNotificationRepository.deleteById(key);
    }

    /**
     * TODO: 데이터 조회 예외 처리
     */
    public Boolean hasFcmNotification(Long key) {
        return fcmNotificationRepository.existsById(key);
    }

    @Transactional
    public List<FcmToken> findFcmTokensByButtonAuthority(Meeting meeting, ButtonAuthority buttonAuthority) {
        return participationRepository.findFcmTokensByMeetingAndButtonAuthority(meeting, buttonAuthority);
    }

    /**
     * 알람 내용을 firebase 서버에 전달하는 method입니다.
     */
    public void sendNotificationByToken(FcmNotificationRequestDto fcmNotificationRequestDto) {
        for (FcmToken fcmToken : fcmNotificationRequestDto.getTargetTokenList()) {
            Notification notification = Notification.builder()
                    .setTitle(fcmNotificationRequestDto.getTitle())
                    .setBody(fcmNotificationRequestDto.getBody())
                    // .setImage(fcmNotificationRequestDto.getImg())
                    .build();
            // 기기에 맞는 token과 내용을 담는 부분입니다.
            Message message = Message.builder()
                    .setToken(fcmToken.getFcmToken())
                    .setNotification(notification)
                    .build();

            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                log.error("Failed to send Notification", e);
                throw new InvalidValueException(ErrorCode.INVALID_FCM_UPLOAD);
            }
        }
    }

    public void createFcmTimer(Long key, LocalDateTime startTime) {
        FcmTimer fcmTimer = FcmTimer.builder()
                .id(key)
                .startTime(startTime)
                .build();
        fcmTimerRepository.save(fcmTimer);
    }

}
