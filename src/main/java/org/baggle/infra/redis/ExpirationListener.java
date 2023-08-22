package org.baggle.infra.redis;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.service.FcmNotificationProvider;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.baggle.domain.fcm.service.FcmService;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.MeetingStatus;
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
@Transactional
@Component
public class ExpirationListener implements MessageListener {
    private final FcmNotificationService fcmNotificationService;
    private final FcmService fcmService;
    private final MeetingRepository meetingRepository;
    private final FcmNotificationProvider fcmNotificationProvider;

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
        FcmNotificationRequestDto fcmNotificationRequestDto = createFcmNotificationRequestDto(parts[0], Long.parseLong(parts[1]));
        fcmNotificationService.sendNotificationByToken(fcmNotificationRequestDto, Long.parseLong(parts[1]));
        log.info("########## onMessage message " + message.toString());
    }

    private Meeting getMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEETING_NOT_FOUND));
    }

    private List<FcmToken> getFcmTokens(Long meetingId) {
        return fcmService.findFcmTokens(meetingId);
    }

    private FcmNotificationRequestDto createFcmNotificationRequestDto(String dataType, Long meetingId) {
        List<FcmToken> fcmTokens = getFcmTokens(meetingId);
        String title = getNotificationTitle(dataType);
        String body = getNotificationBody(dataType);
        return FcmNotificationRequestDto.of(fcmTokens, title, body);
    }

    private boolean validateRedisDataType(String dataType) {
        if (!(getRedisDataType(dataType) == RedisDataType.FCM_TIMER ||
                getRedisDataType(dataType) == RedisDataType.FCM_NOTIFICATION))
            return false;
        return true;
    }

    private void createEmergencyTimerWithRedisDataAndMeetingId(String dataType, Long meetingId) {
        if (getRedisDataType(dataType) == RedisDataType.FCM_NOTIFICATION)
            createFcmTimer(meetingId);
    }

    private RedisDataType getRedisDataType(String dataType) {
        return RedisDataType.getEnumRedisDataTypeFromString(dataType);
    }

    private void createFcmTimer(Long meetingId) {
        LocalDateTime startTime = LocalDateTime.now();
        fcmNotificationService.createFcmTimer(meetingId, startTime);
    }

    private void updateMeetingStatus(Long meetingId, String dateType) {
        Meeting meeting = getMeeting(meetingId);
        if (getRedisDataType(dateType) == RedisDataType.FCM_NOTIFICATION)
            meeting.updateMeetingStatusInto(MeetingStatus.ONGOING);
        else if (getRedisDataType(dateType) == RedisDataType.FCM_TIMER)
            meeting.updateMeetingStatusInto(MeetingStatus.TERMINATION);
    }

    private String getNotificationTitle(String dateType) {
        if (getRedisDataType(dateType) == RedisDataType.FCM_NOTIFICATION)
            return fcmNotificationProvider.getEmergencyNotificationTitle();
        return fcmNotificationProvider.getTerminationNotificationTitle();
    }

    private String getNotificationBody(String dateType) {
        if (getRedisDataType(dateType) == RedisDataType.FCM_NOTIFICATION)
            return fcmNotificationProvider.getEmergencyNotificationBody();
        return fcmNotificationProvider.getTerminationNotificationBody();
    }

}
