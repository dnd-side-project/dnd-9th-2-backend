package org.baggle.domain.fcm.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.fcm.service.FcmNotificationProvider;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.baggle.domain.meeting.domain.ButtonAuthority;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.MeetingStatus;
import org.baggle.domain.meeting.service.MeetingDetailService;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    private final MeetingDetailService meetingDetailService;
    private final FcmNotificationService fcmNotificationService;
    private final FcmNotificationProvider fcmNotificationProvider;

    /**
     * 시작 1시간 전 모임을 찾아 알람을 전송하는 메서드
     * Cron 표현식을 사용한 작업 예약
     * 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)
     */
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void notificationScheduleTask() {
        LocalDateTime now = LocalDateTime.now();
        List<Meeting> notificationMeeting = meetingDetailService.findMeetingsInRange(now, 58, 60);
        for (Meeting m : notificationMeeting) {
            if (m.getMeetingStatus() != MeetingStatus.SCHEDULED) continue;
            m.updateMeetingStatusIntoConfirmation();
            sendNotificationByButtonAuthority(m, ButtonAuthority.OWNER);
            sendNotificationByButtonAuthority(m, ButtonAuthority.NON_OWNER);
            fcmNotificationService.createFcmNotification(m.getId());
            log.info("meeting information - ID {}, date {}, time {}", m.getId(), m.getDate(), m.getTime());
        }
    }

    private List<FcmToken> getFcmTokens(Meeting meeting, ButtonAuthority buttonAuthority){
        return fcmNotificationService.findFcmTokensByButtonAuthority(meeting, buttonAuthority);
    }

    private FcmNotificationRequestDto createFcmNotificationRequestDto(Meeting meeting, ButtonAuthority buttonAuthority){
        List<FcmToken> fcmTokens = getFcmTokens(meeting, buttonAuthority);
        String title = getNotificationTitleWithButtonAuthority(buttonAuthority);
        String body = getNotificationBodyWithButtonAuthority(buttonAuthority);
        return FcmNotificationRequestDto.of(fcmTokens, title, body);
    }

    private void sendNotificationByButtonAuthority(Meeting meeting, ButtonAuthority buttonAuthority){
        FcmNotificationRequestDto fcmNotificationRequestDto = createFcmNotificationRequestDto(meeting, buttonAuthority);
        fcmNotificationService.sendNotificationByToken(fcmNotificationRequestDto);
    }

    private String getNotificationTitleWithButtonAuthority(ButtonAuthority buttonAuthority){
        if(buttonAuthority == ButtonAuthority.OWNER)
            return fcmNotificationProvider.getButtonOwnerNotificationTitle();
        return fcmNotificationProvider.getConfirmationNotificationTitle();
    }

    private String getNotificationBodyWithButtonAuthority(ButtonAuthority buttonAuthority){
        if(buttonAuthority == ButtonAuthority.OWNER)
            return fcmNotificationProvider.getButtonOwnerNotificationBody();
        return fcmNotificationProvider.getConfirmationNotificationBody();
    }

}
