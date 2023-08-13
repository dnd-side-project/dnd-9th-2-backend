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
        if (!validateRedisDataType(parts[0])) return;
        createEmergencyTimerWithRedisDataAndMeetingId(parts[0], Long.parseLong(parts[1]));
        updateMeetingStatus(Long.parseLong(parts[1]), parts[0]);
        String title = getNotificationTitle(parts[0]);
        String body = getNotificationBody(parts[0]);
        List<FcmToken> fcmTokens = getFcmTokens(Long.parseLong(parts[1]));
        // fcmNotificationService.sendNotificationByToken(FcmNotificationRequestDto.of(fcmTokens, title, body));
        log.info("########## onMessage message " + message.toString());
    }

    private Meeting getMeeting(Long meetingId){
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEETING_NOT_FOUND));
    }
    private List<FcmToken> getFcmTokens(Long meetingId){
        return fcmService.findFcmTokens(meetingId);
    }
    private boolean validateRedisDataType(String dataType){
        if(!(getRedisDataType(dataType) == RedisDataType.FCM_TIMER ||
                getRedisDataType(dataType) == RedisDataType.FCM_NOTIFICATION))
            return false;
        return true;
    }
    private void createEmergencyTimerWithRedisDataAndMeetingId(String dataType, Long meetingId){
        if (getRedisDataType(dataType) == RedisDataType.FCM_NOTIFICATION)
            createFcmTimer(meetingId);
    }
    private RedisDataType getRedisDataType(String dataType){
        return RedisDataType.getEnumRedisDataTypeFromString(dataType);
    }

    private void createFcmTimer(Long meetingId){
        LocalDateTime startTime = LocalDateTime.now();
        fcmNotificationService.createFcmTimer(meetingId, startTime);
    }

    private void updateMeetingStatus(Long meetingId, String dateType){
        Meeting meeting = getMeeting(meetingId);
        if(getRedisDataType(dateType) == RedisDataType.FCM_NOTIFICATION)
            meeting.updateMeetingStatusIntoOngoing();
        else if (getRedisDataType(dateType) == RedisDataType.FCM_TIMER)
            meeting.updateMeetingStatusIntoTermination();
    }
    private String getNotificationTitle(String dateType){
        if(getRedisDataType(dateType) == RedisDataType.FCM_NOTIFICATION)
            return "긴급소집!!";
        return "긴급소집 종료";
    }
    private String getNotificationBody(String dateType){
        if(getRedisDataType(dateType) == RedisDataType.FCM_NOTIFICATION)
            return "지금 당장 사진을 인증하세요!!";
        return "긴급 소집이 종료되었습니다. 사진을 확인하세요!";
    }

}
