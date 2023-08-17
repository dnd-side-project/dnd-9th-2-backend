package org.baggle.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.repository.FcmRepository;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.fcm.service.FcmNotificationProvider;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.feed.dto.request.FeedUploadRequestDto;
import org.baggle.domain.feed.dto.response.FeedNotificationResponseDto;
import org.baggle.domain.feed.dto.response.FeedUploadResponseDto;
import org.baggle.domain.feed.repository.FeedRepository;
import org.baggle.domain.meeting.domain.ButtonAuthority;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.MeetingStatus;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.domain.user.domain.User;
import org.baggle.global.common.ImageType;
import org.baggle.global.error.exception.ConflictException;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.ForbiddenException;
import org.baggle.global.error.exception.InvalidValueException;
import org.baggle.infra.s3.S3Provider;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.baggle.global.error.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional
@Service
public class FeedService {
    private final ParticipationRepository participationRepository;
    private final FeedRepository feedRepository;
    private final S3Provider s3Provider;
    private final FcmNotificationService fcmNotificationService;
    private final FcmRepository fcmRepository;
    private final MeetingRepository meetingRepository;
    private final FcmNotificationProvider fcmNotificationProvider;

    /**
     * 피드를 업로드하는 메서드입니다.
     * throw 모임에 참가자가 없는 경우
     * throw 이미 인증을 완료한 경우
     * throw 긴급소집 이벤트가 진행 중이 아닌 경우
     */
    public FeedUploadResponseDto feedUpload(FeedUploadRequestDto requestDto, MultipartFile feedImage) {
        Participation participation = getParticipation(requestDto.getMemberId());
        duplicationParticipation(participation);
        validateCertificationTime(participation.getMeeting());
        String imgUrl = s3Provider.uploadFile(feedImage, ImageType.FEED.getImageType());
        Feed feed = Feed.createParticipationWithFeedImg(participation, imgUrl);
        feedRepository.save(feed);
        return FeedUploadResponseDto.of(feed.getId(), feed.getFeedImageUrl());
    }

    /**
     * 긴급소집 알람을 전송하는 api입니다.
     * throw 모임에 참가자가 없는 경우
     * throw 버튼 활성화 가능 시간이 아닌 경우
     */
    public FeedNotificationResponseDto uploadNotification(Long requestId, LocalDateTime authorizationTime) {
        Participation participation = getParticipation(requestId);
        validateMeetingStatusForConfirmation(participation.getMeeting());
        validateButtonOwner(participation);
        broadcastNotification(participation.getMeeting());
        startEmergencyNotificationEvent(participation, authorizationTime);
        return FeedNotificationResponseDto.of(participation.getMeeting(), authorizationTime);
    }

    private Meeting getMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
    }

    private Participation getParticipation(Long participationId) {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException(PARTICIPATION_NOT_FOUND));
    }

    private void validateButtonOwner(Participation participation){
        if(participation.getButtonAuthority() != ButtonAuthority.OWNER)
            throw new ForbiddenException(NOT_MATCH_BUTTON_OWNER);
    }

    private void validateMeetingStatusForConfirmation(Meeting meeting) {
        if (meeting.getMeetingStatus() != MeetingStatus.CONFIRMATION)
            throw new InvalidValueException(INVALID_MEETING_TIME);
    }

    private void validateCertificationTime(Meeting meeting) {
        if (meeting.getMeetingStatus() != MeetingStatus.ONGOING)
            throw new InvalidValueException(INVALID_CERTIFICATION_TIME);
    }

    private void duplicationParticipation(Participation participation) {
        Optional<Feed> feed = feedRepository.findByParticipationId(participation.getId());
        if (feed.isPresent())
            throw new ConflictException(DUPLICATE_FEED);
    }

    private void broadcastNotification(Meeting meeting) {
        List<FcmToken> fcmTokens = fcmRepository.findByUserParticipationsMeetingId(meeting.getId());
        String title = fcmNotificationProvider.getEmergencyNotificationTitle();
        String body = fcmNotificationProvider.getEmergencyNotificationBody();
        fcmNotificationService.sendNotificationByToken(FcmNotificationRequestDto.of(fcmTokens, title, body));
    }

    private void startEmergencyNotificationEvent(Participation participation, LocalDateTime authorizationTime) {
        fcmNotificationService.deleteFcmNotification(participation.getMeeting().getId());
        fcmNotificationService.createFcmTimer(participation.getMeeting().getId(), authorizationTime);
        Meeting meeting = getMeeting(participation.getMeeting().getId());
        meeting.updateMeetingStatusIntoOngoing();
    }
}
