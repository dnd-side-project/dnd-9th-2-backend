package org.baggle.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.repository.FcmRepository;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.feed.dto.request.FeedUploadRequestDto;
import org.baggle.domain.feed.dto.response.FeedNotificationResponseDto;
import org.baggle.domain.feed.dto.response.FeedUploadResponseDto;
import org.baggle.domain.feed.repository.FeedRepository;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.domain.meeting.service.MeetingService;
import org.baggle.global.common.ImageType;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.InvalidValueException;
import org.baggle.infra.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.baggle.global.error.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional
@Service
public class FeedService {
    private final ParticipationRepository participationRepository;
    private final FeedRepository feedRepository;
    private final S3Service s3Service;
    private final FcmNotificationService fcmNotificationService;
    private final FcmTimerRepository fcmTimerRepository;
    private final FcmRepository fcmRepository;
    private final MeetingService meetingService;

    /**
     * 피드를 업로드하는 메서드입니다.
     * throw 모임에 참가자가 없는 경우
     * throw 모임이 확정되지 않은 경우
     * throw 긴급소집 이벤트가 진행 중이 아닌 경우
     */
    public FeedUploadResponseDto feedUpload(FeedUploadRequestDto requestDto, MultipartFile feedImage) throws InvalidValueException {
        Participation participation = participationRepository.findById(requestDto.getParticipationId()).orElseThrow(() -> new EntityNotFoundException(PARTICIPATION_NOT_FOUND));
        if (!meetingService.isValidTime(participation.getMeeting()))
            throw new InvalidValueException(INVALID_MEETING_TIME);
        if (!validateCertificationTime(participation.getMeeting()))
            throw new InvalidValueException(INVALID_CERTIFICATION_TIME);
        String imgUrl = s3Service.uploadFile(feedImage, ImageType.FEED.getImageType());
        Feed feed = Feed.createParticipationWithFeedImg(participation, imgUrl);
        feedRepository.save(feed);
        return FeedUploadResponseDto.of(feed.getId());
    }

    /**
     * 긴급소집 알람을 전송하는 api입니다.
     * throw 모임에 참가자가 없는 경우
     * throw 모임이 확정되지 않은 경우
     */
    public FeedNotificationResponseDto uploadNotification(Long requestId) {
        Participation participation = participationRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(PARTICIPATION_NOT_FOUND));
        if (!meetingService.isValidTime(participation.getMeeting()))
            throw new InvalidValueException(INVALID_MEETING_TIME);
        // 알람을 broadcast 하는 code 입니다.
        List<FcmToken> fcmTokens = fcmRepository.findByUserParticipationsMeetingId(participation.getMeeting().getId());
        FcmNotificationRequestDto fcmNotificationRequestDto = FcmNotificationRequestDto.of(fcmTokens, "", "");
        fcmNotificationService.sendNotificationByToken(fcmNotificationRequestDto);
        // 이벤트 로직 5분 타이머를 시작하는 code 입니다.
        LocalDateTime startTime = LocalDateTime.now();
        fcmNotificationService.createFcmTimer(participation.getMeeting().getId(), startTime);
        return FeedNotificationResponseDto.of(participation.getMeeting(), startTime);
    }

    /**
     * 긴급 소집 이벤트 활성화 여부를 확인하는 코드
     * return: 긴급 소집 이벤트 활성화시 True else False
     */
    private Boolean validateCertificationTime(Meeting meeting) {
        return fcmTimerRepository.existsById(meeting.getId());
    }
}
