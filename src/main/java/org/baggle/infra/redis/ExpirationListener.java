package org.baggle.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.baggle.domain.fcm.service.FcmService;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.ErrorCode;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExpirationListener implements MessageListener {
    private final FcmNotificationService fcmNotificationService;
    private final FcmService fcmService;
    private final MeetingRepository meetingRepository;

    /**
     * cache가 만료되었을 때 실행되는 메서드입니다.
     * 긴급알람과 관련된 cache를 소모할 때 알람을 보냅니다.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String[] parts = message.toString().split(":");
        String title = "긴급 소집이 종료되었습니다!!";
        String body = "";
        if (!(parts[0].equals("fcmNotification") || parts[0].equals("fcmTimer"))) return;
        Meeting meeting = getMeeting(Long.parseLong(parts[1]));
        if (parts[0].equals("fcmNotification")) {
            LocalDateTime startTime = LocalDateTime.now();
            fcmNotificationService.createFcmTimer(Long.parseLong(parts[1]), startTime);
            meeting.updateMeetingStatusIntoOngoing();
            title = "긴급 소집!!";
        }
        if (parts[0].equals("fcmNotification"))
            meeting.updateMeetingStatusIntoTermination();
        List<FcmToken> fcmTokens = fcmService.findFcmTokens(Long.parseLong(parts[1]));
        // fcmNotificationService.sendNotificationByToken(FcmNotificationRequestDto.of(fcmTokens, title, body));
        log.info("########## onMessage message " + message.toString());
    }

    private Meeting getMeeting(Long meetingId){
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEETING_NOT_FOUND));
    }
}
