package org.baggle.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmTimer;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.provider.FcmNotificationProvider;
import org.baggle.domain.fcm.repository.FcmNotificationRepository;
import org.baggle.domain.fcm.repository.FcmRepository;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.fcm.provider.FcmMessageSourceProvider;
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
import org.baggle.global.common.ImageType;
import org.baggle.global.error.exception.ConflictException;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.ForbiddenException;
import org.baggle.global.error.exception.InvalidValueException;
import org.baggle.infra.s3.S3Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.baggle.global.error.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional
@Service
public class FeedService {
    private final ParticipationRepository participationRepository;
    private final FeedRepository feedRepository;
    private final S3Provider s3Provider;
    private final FcmNotificationRepository fcmNotificationRepository;
    private final FcmTimerRepository fcmTimerRepository;
    private final FcmRepository fcmRepository;
    private final MeetingRepository meetingRepository;
    private final FcmMessageSourceProvider fcmMessageSourceProvider;
    private final FcmNotificationProvider fcmNotificationProvider;

    public FeedUploadResponseDto feedUpload(FeedUploadRequestDto requestDto, MultipartFile feedImage) {
        Participation participation = getParticipation(requestDto.getMemberId());
        duplicationParticipation(participation);
        validateCertificationTime(participation.getMeeting());
        String imgUrl = uploadImageToS3(feedImage);
        Feed feed = Feed.createFeed(participation, imgUrl);
        saveFeed(feed);
        return FeedUploadResponseDto.of(feed.getId(), feed.getFeedImageUrl());
    }

    public FeedNotificationResponseDto uploadNotification(Long requestId, LocalDateTime authorizationTime) {
        Participation participation = getParticipation(requestId);
        validateMeetingStatusForConfirmation(participation.getMeeting());
        validateButtonOwner(participation);
        broadcastNotification(participation, participation.getMeeting());
        startEmergencyNotificationEvent(participation, authorizationTime);
        return FeedNotificationResponseDto.of(participation.getMeeting(), authorizationTime);
    }

    private String uploadImageToS3(MultipartFile feedImage) {
        return s3Provider.uploadFile(feedImage, ImageType.FEED.getImageType());
    }

    private void saveFeed(Feed feed) {
        feedRepository.save(feed);
    }

    private Meeting getMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
    }

    private Participation getParticipation(Long participationId) {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException(PARTICIPATION_NOT_FOUND));
    }

    private void validateButtonOwner(Participation participation) {
        if (participation.getButtonAuthority() != ButtonAuthority.OWNER)
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

    private void broadcastNotification(Participation requestParticipation, Meeting meeting) {
        FcmNotificationRequestDto fcmNotificationRequestDto = createFcmNotificationRequestDto(requestParticipation, meeting.getId());
        fcmNotificationProvider.sendNotificationByToken(fcmNotificationRequestDto, meeting.getId());
    }

    private FcmNotificationRequestDto createFcmNotificationRequestDto(Participation participation, Long meetingId) {
        List<FcmToken> fcmTokens = fcmRepository.findByUserParticipationsMeetingId(meetingId);
        fcmTokens = fcmTokens.stream().filter(fcmToken -> !Objects.isNull(fcmToken.getFcmToken())).collect(Collectors.toList());
        deleteFcmTokenOfRequestParticipation(fcmTokens, participation);
        String title = fcmMessageSourceProvider.getEmergencyNotificationTitle();
        String body = fcmMessageSourceProvider.getEmergencyNotificationBody();
        return FcmNotificationRequestDto.of(fcmTokens, title, body);
    }

    private void deleteFcmTokenOfRequestParticipation(List<FcmToken> fcmTokens, Participation participation) {
        FcmToken fcmToken = participation.getUser().getFcmToken();
        fcmTokens.remove(fcmToken);
    }

    private void startEmergencyNotificationEvent(Participation participation, LocalDateTime authorizationTime) {
        deleteFcmNotification(participation.getMeeting().getId());
        createFcmTimer(participation.getMeeting().getId(), authorizationTime);
        Meeting meeting = getMeeting(participation.getMeeting().getId());
        meeting.updateMeetingStatusInto(MeetingStatus.ONGOING);
    }

    public void createFcmTimer(Long key, LocalDateTime startTime) {
        FcmTimer fcmTimer = FcmTimer.createFcmTimer(key, startTime);
        fcmTimerRepository.save(fcmTimer);
    }

    public void deleteFcmNotification(Long key) {
        fcmNotificationRepository.deleteById(key);
    }
}
