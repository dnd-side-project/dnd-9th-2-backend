package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmTimer;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.FcmNotificationRequestDto;
import org.baggle.domain.fcm.repository.FcmRepository;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.fcm.service.FcmNotificationProvider;
import org.baggle.domain.fcm.service.FcmNotificationService;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.meeting.domain.*;
import org.baggle.domain.meeting.dto.request.UpdateMeetingInfoRequestDto;
import org.baggle.domain.meeting.dto.response.MeetingDetailResponseDto;
import org.baggle.domain.meeting.dto.response.ParticipationDetailResponseDto;
import org.baggle.domain.meeting.dto.response.UpdateMeetingInfoResponseDto;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.domain.report.domain.Report;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.ForbiddenException;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.baggle.global.common.TimeConverter.convertToLocalDateTime;
import static org.baggle.global.error.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional
@Service
public class MeetingDetailService {
    private final MeetingRepository meetingRepository;
    private final ParticipationRepository participationRepository;
    private final FcmTimerRepository fcmTimerRepository;
    private final FcmRepository fcmRepository;
    private final FcmNotificationProvider fcmNotificationProvider;
    private final FcmNotificationService fcmNotificationService;

    public MeetingDetailResponseDto findMeetingDetail(Long userId, Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        FcmTimer certificationTime = getFcmTimer(meetingId);
        List<Participation> participationList = sortParticipationAlongAuthorization(meeting);
        Participation requestParticipation = getRequestParticipationWithUserId(participationList, userId);
        List<ParticipationDetailResponseDto> participationDetails = createParticipationDetailResDtoList(participationList, requestParticipation);
        return MeetingDetailResponseDto.of(meeting, convertToLocalDateTime(meeting.getDate(), meeting.getTime()),
                certificationTime.getStartTime(), participationDetails);
    }

    public UpdateMeetingInfoResponseDto updateMeetingInfo(Long userId, UpdateMeetingInfoRequestDto requestDto) {
        Meeting meeting = getMeeting(requestDto.getMeetingId());
        validateMeetingHost(meeting.getId(), userId);
        validateMeetingStatus(meeting);
        validateMeetingDateTime(meeting, requestDto.getDateTime());
        meeting.updateMeetingInfo(requestDto.getTitle(), requestDto.getPlace(), requestDto.getDateTime(), requestDto.getMemo());
        return UpdateMeetingInfoResponseDto.of(meeting.getId(), meeting.getTitle(), meeting.getPlace(),
                LocalDateTime.of(meeting.getDate(), meeting.getTime()), meeting.getMemo());
    }

    public void deleteMeetingInfo(Long userId, Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        validateParticipation(meeting, userId);
        validateMeetingHost(meeting.getId(), userId);
        validateMeetingStatus(meeting);
        broadcastNotification(meeting);
        deleteMeeting(meeting.getId());
    }

    private List<ParticipationDetailResponseDto> createParticipationDetailResDtoList(List<Participation> participationList,
                                                                                     Participation requestParticipation) {
        return participationList.stream()
                .map(participation ->
                        ParticipationDetailResponseDto.of(participation,
                                isReportedFeed(requestParticipation.getReports(), participation.getFeed())))
                .toList();
    }

    private boolean isReportedFeed(List<Report> reportList, Feed feed) {
        if (Objects.isNull(feed)) return false;
        return reportList.stream()
                .anyMatch(report -> Objects.equals(report.getFeed().getId(), feed.getId()));
    }

    private Participation getRequestParticipationWithUserId(List<Participation> participationList, Long userId) {
        return participationList.stream()
                .filter(participation -> Objects.equals(participation.getUser().getId(), userId))
                .findAny()
                .orElseThrow(() -> new InvalidValueException(INVALID_MEETING_PARTICIPATION));
    }

    private Meeting getMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
    }

    public void deleteMeeting(Long meetingId) {
        meetingRepository.deleteById(meetingId);
    }

    private FcmTimer getFcmTimer(Long fcmTimerId) {
        return fcmTimerRepository.findById(fcmTimerId)
                .orElse(FcmTimer.createFcmTimerWithNull());
    }

    public List<Meeting> findMeetingsInRange(int from, int to, MeetingStatus meetingStatus) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDateTime = now.plusSeconds(from);
        LocalDateTime toDateTime = now.plusSeconds(to);
        return meetingRepository.findMeetingsWithinTimeRangeAlongMeetingStatus(
                fromDateTime,
                toDateTime,
                meetingStatus);
    }

    private List<Participation> sortParticipationAlongAuthorization(Meeting meeting) {
        List<Participation> participationList = meeting.getParticipations();
        return participationList.stream()
                .sorted(Comparator
                        .comparing((Participation p) -> p.getMeetingAuthority() == MeetingAuthority.HOST ? 0 : 1)
                        .thenComparing(p -> p.getButtonAuthority() == ButtonAuthority.OWNER ? 0 : 1))
                .toList();
    }

    private List<Meeting> findMeetingsInRangeForUser(Long userId, LocalDateTime localDateTime, int from, int to) {
        LocalDateTime fromDateTime = localDateTime.plusMinutes(from);
        LocalDateTime toDateTime = localDateTime.plusMinutes(to);
        return meetingRepository.findMeetingsWithinTimeRange(
                userId,
                fromDateTime,
                toDateTime);
    }

    private void validateParticipation(Meeting meeting, Long userId) {
        boolean isValidParticipation = meeting.getParticipations().stream()
                .anyMatch(participation -> Objects.equals(participation.getUser().getId(), userId));
        if (!isValidParticipation)
            throw new InvalidValueException(INVALID_MEETING_PARTICIPATION);
    }

    private void validateMeetingHost(Long meetingId, Long userId) {
        Optional<Participation> participation = participationRepository.findByUserIdAndMeetingId(userId, meetingId);
        if (participation.get().getMeetingAuthority() != MeetingAuthority.HOST)
            throw new ForbiddenException(INVALID_MEETING_AUTHORITY);
    }

    private void validateMeetingStatus(Meeting meeting) {
        if (meeting.getMeetingStatus() != MeetingStatus.SCHEDULED)
            throw new InvalidValueException(INVALID_MEETING_TIME);
    }

    private void validateMeetingDateTime(Meeting meeting, LocalDateTime requestDateTime) {
        if (requestDateTime == null) return;
        validateMeetingTime(meeting, requestDateTime);
    }

    private void validateMeetingTime(Meeting meeting, LocalDateTime requestDateTime) {
        meeting.getParticipations().forEach(participation ->
                isMeetingInDeadline(meeting.getId(), participation.getUser().getId(), requestDateTime));
    }

    public void isMeetingInDeadline(Long meetingId, Long userId, LocalDateTime meetingTime) {
        List<Meeting> meetings = findMeetingsInRangeForUser(userId, meetingTime, -60, 60)
                .stream()
                .filter(m -> m.getId() != meetingId)
                .toList();
        if (!meetings.isEmpty())
            throw new InvalidValueException(UNAVAILABLE_MEETING_TIME);
    }

    private void broadcastNotification(Meeting meeting) {
        FcmNotificationRequestDto fcmNotificationRequestDto = createFcmNotificationRequestDto(meeting);
        fcmNotificationService.sendNotificationByToken(fcmNotificationRequestDto, meeting.getId());
    }

    private FcmNotificationRequestDto createFcmNotificationRequestDto(Meeting meeting) {
        List<FcmToken> fcmTokens = fcmRepository.findByUserParticipationsMeetingId(meeting.getId());
        fcmTokens = fcmTokens.stream().filter(fcmToken -> !Objects.isNull(fcmToken.getFcmToken())).toList();
        String title = fcmNotificationProvider.getDeleteNotificationTitle();
        String body = fcmNotificationProvider.getDeleteNotificationBody(meeting.getTitle());
        return FcmNotificationRequestDto.of(fcmTokens, title, body);
    }
}
