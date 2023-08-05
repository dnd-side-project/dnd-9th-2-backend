package org.baggle.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FCMToken;
import org.baggle.domain.fcm.dto.request.FCMNotificationRequestDto;
import org.baggle.domain.fcm.repository.FCMRepository;
import org.baggle.domain.user.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class FCMNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final FCMRepository fcmRepository;

    /**
     * 알람 내용을 firebase 서버에 전달하는 method입니다.
     * @throws FirebaseMessagingException
     */
    public void sendNotificationByToken(FCMNotificationRequestDto fcmNotificationRequestDto) throws FirebaseMessagingException {
        // user에 맞는 FCM token을 찾는 부분입니다.
        List<FCMToken> fcmTokenList = fcmNotificationRequestDto.getTargetUserIdList()
                .stream()
                .map(fcmRepository::findByUserId)
                .toList();
        for(FCMToken fcmToken: fcmTokenList){
            // 알람의 내용을 만드는 부분입니다.
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
