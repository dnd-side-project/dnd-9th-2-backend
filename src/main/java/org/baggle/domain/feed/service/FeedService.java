package org.baggle.domain.feed.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.repository.FcmRepository;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.feed.dto.request.FeedUploadRequestDto;
import org.baggle.domain.feed.dto.response.FeedNotificationResponseDto;
import org.baggle.domain.feed.dto.response.FeedUploadResponseDto;
import org.baggle.domain.feed.repository.FeedRepository;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.domain.user.domain.User;
import org.baggle.global.common.ImageType;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.infra.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.baggle.global.error.exception.ErrorCode.PARTICIPATION_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class FeedService {
    private final ParticipationRepository participationRepository;
    private final FeedRepository feedRepository;
    private final S3Service s3Service;
    private final FcmNotificationService fcmNotificationService;
    private final FcmRepository fcmRepository;

    public FeedUploadResponseDto createFeedUpload(User user, FeedUploadRequestDto requestDto, MultipartFile feedImage) {
        Participation participation = participationRepository.findById(requestDto.getParticipationId()).orElseThrow(() -> new EntityNotFoundException(PARTICIPATION_NOT_FOUND));
        String imgUrl = s3Service.uploadFile(feedImage, ImageType.FEED.getImageType());
        Feed feed = Feed.createParticipationWithFeedImg(participation, imgUrl);
        feedRepository.save(feed);
        return FeedUploadResponseDto.of(feed.getId());
    }

    public FeedNotificationResponseDto uploadNotification(Long requestId) throws FirebaseMessagingException {
        Participation participation = participationRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(PARTICIPATION_NOT_FOUND));
        // 알람을 broadcast 하는 code 입니다.
        List<FcmToken> fcmTokens = fcmRepository.findByUserParticipationsMeetingId(participation.getMeeting().getId());
        FcmNotificationRequestDto fcmNotificationRequestDto = FcmNotificationRequestDto.of(fcmTokens, "", "");
        fcmNotificationService.sendNotificationByToken(fcmNotificationRequestDto);
        // 이벤트 로직 5분 타이머를 시작하는 code 입니다.
        LocalDateTime startTime = LocalDateTime.now();
        fcmNotificationService.createFcmTimer(participation.getMeeting().getId(), startTime);
        return FeedNotificationResponseDto.of(participation.getMeeting(), startTime);
    }
}
