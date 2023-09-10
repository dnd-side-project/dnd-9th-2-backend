package org.baggle.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmNotification;
import org.baggle.domain.fcm.domain.FcmTimer;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.repository.FcmNotificationRepository;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmNotificationRepository fcmNotificationRepository;
    private final FcmTimerRepository fcmTimerRepository;

    public void createFcmNotification(Long key) {
        FcmNotification fcmNotification = FcmNotification.builder()
                .id(key)
                .isNotified(Boolean.TRUE)
                .build();
        fcmNotificationRepository.save(fcmNotification);
    }

    public void deleteFcmNotification(Long key) {
        fcmNotificationRepository.deleteById(key);
    }

    public void sendNotificationByToken(FcmNotificationRequestDto fcmNotificationRequestDto, Long meetingId) {
        for (FcmToken fcmToken : fcmNotificationRequestDto.getTargetTokenList()) {
            Notification notification = createNotification(fcmNotificationRequestDto.getTitle(), fcmNotificationRequestDto.getBody());
            Message message = createMessage(notification, fcmToken, meetingId);
            sendNotification(message);
        }
    }

    private Notification createNotification(String title, String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                // .setImage(fcmNotificationRequestDto.getImg())
                .build();
    }

    private Message createMessage(Notification notification, FcmToken fcmToken, Long meetingId) {
        return Message.builder()
                .setToken(fcmToken.getFcmToken())
                .putData("meetingId", meetingId.toString())
                .setNotification(notification)
                .build();
    }

    private void sendNotification(Message message) {
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send Notification", e);
            throw new InvalidValueException(ErrorCode.INVALID_FCM_UPLOAD);
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
