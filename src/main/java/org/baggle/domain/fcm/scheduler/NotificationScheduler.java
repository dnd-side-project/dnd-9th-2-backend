package org.baggle.domain.fcm.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.baggle.domain.meeting.domain.ButtonAuthority;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.service.MeetingService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * logic:
 * 1. 모임 조회 -> 서버 오차 범위 1분 적용
 * 2. redis cache 에서 알람 전송 여부 확인
 * 3. 긴급버튼 알람 전송시 cache 소모
 * 4. 만약 긴급버튼 알람을 전송하지 못할 경우 redis Cache(47분) 만료 기간에 맞춰 긴급버튼 알람 전송
 * 5. cache 소모
 */
@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class NotificationScheduler {
    private final MeetingService meetingService;
    private final FcmNotificationService fcmNotificationService;

    /**
     * 시작 1시간 전 모임을 찾아 알람을 전송하는 메서드
     * Cron 표현식을 사용한 작업 예약
     * 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)
     */
    @Scheduled(cron = "0 * * * * *")
    public void notificationScheduleTask() throws FirebaseMessagingException {
        List<Meeting> notificationMeeting = meetingService.findMeetingsInRange(59, 60);
        for (Meeting m : notificationMeeting) {
            if (fcmNotificationService.hasFcmNotification(m.getId())) continue;

            List<FcmToken> ownerFcmTokens = fcmNotificationService.findFcmTokensByButtonAuthority(m, ButtonAuthority.OWNER);
            List<FcmToken> nonOwnerFcmTokens = fcmNotificationService.findFcmTokensByButtonAuthority(m, ButtonAuthority.NON_OWNER);

//            fcmNotificationService.sendNotificationByToken(FcmNotificationRequestDto.of(ownerFcmTokens, "", ""));
//            fcmNotificationService.sendNotificationByToken(FcmNotificationRequestDto.of(nonOwnerFcmTokens, "", ""));
            fcmNotificationService.createFcmNotification(m.getId());
            log.info("meeting information - ID {}, date {}, time {}", m.getId(), m.getDate(), m.getTime());
        }
    }

}
