package org.baggle.domain.fcm.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.service.FcmNotificationProvider;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.baggle.domain.meeting.domain.ButtonAuthority;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.MeetingStatus;
import org.baggle.domain.meeting.service.MeetingDetailService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor
public class NotificationScheduler {
    private final MeetingDetailService meetingDetailService;
    private final FcmNotificationService fcmNotificationService;
    private final FcmNotificationProvider fcmNotificationProvider;

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void notificationScheduleTask() {
        List<Meeting> notificationMeeting = meetingDetailService.findMeetingsInRange(3540, 3600, MeetingStatus.SCHEDULED);
        for (Meeting m : notificationMeeting) {
            m.updateMeetingStatusInto(MeetingStatus.CONFIRMATION);
            sendNotificationByButtonAuthority(m, ButtonAuthority.OWNER);
            sendNotificationByButtonAuthority(m, ButtonAuthority.NON_OWNER);
            fcmNotificationService.createFcmNotification(m.getId());
            log.info("meeting information - date {}, time {}", m.getDate(), m.getTime());
        }
    }

    private List<FcmToken> findFcmTokensByButtonAuthority(Meeting meeting, ButtonAuthority buttonAuthority) {
        List<FcmToken> fcmTokenList = fcmNotificationService.findFcmTokensByButtonAuthority(meeting, buttonAuthority);
        return fcmTokenList.stream().filter(fcmToken -> !Objects.isNull(fcmToken)).toList();
    }

    private FcmNotificationRequestDto createFcmNotificationRequestDto(Meeting meeting, ButtonAuthority buttonAuthority) {
        List<FcmToken> fcmTokens = findFcmTokensByButtonAuthority(meeting, buttonAuthority);
        fcmTokens.forEach(fcmToken -> log.info("fcmList", fcmToken));
        String title = getNotificationTitleWithButtonAuthority(buttonAuthority);
        String body = getNotificationBodyWithButtonAuthority(buttonAuthority);
        return FcmNotificationRequestDto.of(fcmTokens, title, body);
    }

    private void sendNotificationByButtonAuthority(Meeting meeting, ButtonAuthority buttonAuthority) {
        FcmNotificationRequestDto fcmNotificationRequestDto = createFcmNotificationRequestDto(meeting, buttonAuthority);
        fcmNotificationService.sendNotificationByToken(fcmNotificationRequestDto, meeting.getId());
    }

    private String getNotificationTitleWithButtonAuthority(ButtonAuthority buttonAuthority) {
        if (buttonAuthority == ButtonAuthority.OWNER)
            return fcmNotificationProvider.getButtonOwnerNotificationTitle();
        return fcmNotificationProvider.getConfirmationNotificationTitle();
    }

    private String getNotificationBodyWithButtonAuthority(ButtonAuthority buttonAuthority) {
        if (buttonAuthority == ButtonAuthority.OWNER)
            return fcmNotificationProvider.getButtonOwnerNotificationBody();
        return fcmNotificationProvider.getConfirmationNotificationBody();
    }

}
