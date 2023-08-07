package org.baggle.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExpirationListener implements MessageListener {
    private final FcmNotificationService fcmNotificationService;

    /**
     * cache가 만료되었을 때 실행되는 메서드입니다.
     * 긴급알람과 관련된 cache를 소모할 때 알람을 보냅니다.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String[] parts = message.toString().split(":");
        if (!parts[0].equals("fcmNotification")) return;
        List<FcmToken> fcmTokens = fcmNotificationService.findFcmTokens(Long.parseLong(parts[1]));
        System.out.println(fcmTokens);
        //            fcmNotificationService.sendNotificationByToken(FcmNotificationRequestDto.of(fcmTokens, "", ""));
        System.out.println("########## onMessage pattern " + new String(pattern) + " | " + message.toString());
    }
}
