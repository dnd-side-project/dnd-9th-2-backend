package org.baggle.domain.fcm.provider;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.baggle.global.error.exception.ErrorCode.INVALID_FCM_UPLOAD;

@Slf4j
@RequiredArgsConstructor
@Component
public class FcmNotificationProvider {
    private final FirebaseMessaging firebaseMessaging;

    public void broadcastFcmNotification(FcmNotificationRequestDto fcmNotificationRequestDto, Long meetingId) {
        List<FcmToken> fcmTokenList = fcmNotificationRequestDto.getTargetTokenList();
        fcmTokenList.forEach(fcmToken -> sendNotification(fcmToken, fcmNotificationRequestDto, meetingId));
    }

    private Notification createNotification(String title, String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

    private Message createMessage(Notification notification, FcmToken fcmToken, Long meetingId) {
        return Message.builder()
                .setToken(fcmToken.getFcmToken())
                .putData("meetingId", meetingId.toString())
                .setNotification(notification)
                .build();
    }

    private void sendNotification(FcmToken fcmToken, FcmNotificationRequestDto fcmNotificationRequestDto, Long meetingId) {
        try {
            Notification notification = createNotification(fcmNotificationRequestDto.getTitle(), fcmNotificationRequestDto.getBody());
            Message message = createMessage(notification, fcmToken, meetingId);
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send Notification", e);
            throw new InvalidValueException(INVALID_FCM_UPLOAD);
        }
    }
}
