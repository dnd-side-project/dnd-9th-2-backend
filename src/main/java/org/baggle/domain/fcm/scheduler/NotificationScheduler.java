package org.baggle.domain.fcm.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.service.MeetingService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class NotificationScheduler {
    private final MeetingService meetingService;
    private final FcmNotificationService fcmNotificationService;

    /**
     * Cron 표현식을 사용한 작업 예약
     * 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)
     * <p>
     * 1. 모임 조회 -> 서버 오차 범위 1분 적용
     * 2. redis cache 에서 알람 전송 여부 확인
     */
    @Scheduled(cron = "0 * * * * *")
    public void notificationScheduleTask() throws FirebaseMessagingException {
        List<Meeting> notificationMeeting = meetingService.findMeetingsInRange(0, 60);
        for (Meeting m : notificationMeeting) {
            if (fcmNotificationService.hasFCMNotification(m.getId())) continue;

            List<FcmToken> fcmTokens = fcmNotificationService.findFCMTokens(m.getId());
//            fcmNotificationService.sendNotificationByToken(FcmNotificationRequestDto.of(fcmTokens, "", ""));
            fcmNotificationService.createFCMNotification(m.getId());
            log.info("meeting information - date {}, time {}", m.getDate(), m.getTime());
        }
        long now = System.currentTimeMillis() / 1000;
        log.info("schedule tasks using cron jobs - {}", now);
    }
}
