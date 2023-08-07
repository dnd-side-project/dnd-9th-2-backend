package org.baggle.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmNotification;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.repository.FcmNotificationRepository;
import org.baggle.domain.fcm.repository.FcmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class FcmNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmRepository fcmRepository;
    private final FcmNotificationRepository fcmNotificationRepository;

    /**
     * TODO: Create 예외 처리
     */
    public void createFCMNotification(Long key) {
        FcmNotification fcmNotification = FcmNotification.builder()
                .id(key)
                .isNotified(Boolean.TRUE)
                .build();
        fcmNotificationRepository.save(fcmNotification);
    }

    /**
     * TODO: 데이터 조회 예외 처리
     */
    public Boolean hasFCMNotification(Long key) {
        return fcmNotificationRepository.existsById(key);
    }

    public List<FcmToken> findFCMTokens(Long meetingId) {
        return fcmRepository.findByUserParticipationsMeetingId(meetingId);
    }

    /**
     * 알람 내용을 firebase 서버에 전달하는 method입니다.
     */
    public void sendNotificationByToken(FcmNotificationRequestDto fcmNotificationRequestDto) throws FirebaseMessagingException {

        for (FcmToken fcmToken : fcmNotificationRequestDto.getTargetUserIdList()) {
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

            firebaseMessaging.send(message);
        }
    }

}
