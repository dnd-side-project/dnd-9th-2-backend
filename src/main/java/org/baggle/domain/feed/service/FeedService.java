package org.baggle.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.repository.FcmRepository;
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

    public FeedUploadResponseDto feedUpload(FeedUploadRequestDto requestDto, MultipartFile feedImage) {
        Participation participation = getParticipation(requestDto.getMemberId());
        duplicationParticipation(participation);
        validateCertificationTime(participation.getMeeting());
        String imgUrl = s3Provider.uploadFile(feedImage, ImageType.FEED.getImageType());
        Feed feed = Feed.createParticipationWithFeedImg(participation, imgUrl);
        feedRepository.save(feed);
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
        fcmNotificationService.sendNotificationByToken(fcmNotificationRequestDto, meeting.getId());
    }

    private FcmNotificationRequestDto createFcmNotificationRequestDto(Participation participation, Long meetingId) {
        List<FcmToken> fcmTokens = fcmRepository.findByUserParticipationsMeetingId(meetingId);
        fcmTokens = fcmTokens.stream().filter(fcmToken -> !Objects.isNull(fcmToken.getFcmToken())).toList();
        deleteFcmTokenOfRequestParticipation(fcmTokens, participation);
        String title = fcmNotificationProvider.getEmergencyNotificationTitle();
        String body = fcmNotificationProvider.getEmergencyNotificationBody();
        return FcmNotificationRequestDto.of(fcmTokens, title, body);
    }

    private void deleteFcmTokenOfRequestParticipation(List<FcmToken> fcmTokens, Participation participation){
        FcmToken fcmToken = participation.getUser().getFcmToken();
        System.out.println(Objects.isNull(fcmTokens));
        fcmTokens.remove(fcmToken);
    }

    private void startEmergencyNotificationEvent(Participation participation, LocalDateTime authorizationTime) {
        fcmNotificationService.deleteFcmNotification(participation.getMeeting().getId());
        fcmNotificationService.createFcmTimer(participation.getMeeting().getId(), authorizationTime);
        Meeting meeting = getMeeting(participation.getMeeting().getId());
        meeting.updateMeetingStatusInto(MeetingStatus.ONGOING);
    }
}
