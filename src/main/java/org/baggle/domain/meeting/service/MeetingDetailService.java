package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmTimer;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.MeetingAuthority;
import org.baggle.domain.meeting.domain.MeetingStatus;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.domain.meeting.dto.request.UpdateMeetingInfoRequestDto;
import org.baggle.domain.meeting.dto.response.MeetingDetailResponseDto;
import org.baggle.domain.meeting.dto.response.ParticipationDetailResponseDto;
import org.baggle.domain.meeting.dto.response.UpdateMeetingInfoResponseDto;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.ForbiddenException;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.baggle.global.error.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional
@Service
public class MeetingDetailService {
    private final MeetingRepository meetingRepository;
    private final ParticipationRepository participationRepository;
    private final FcmTimerRepository fcmTimerRepository;

    /**
     * throw 모임이 존재하지 않는 경우
     * throw 모임 참가자가 아닌 경우
     */
    public MeetingDetailResponseDto findMeetingDetail(Long userId, Long requestId) {
        Meeting meeting = getMeeting(requestId);
        validateParticipation(meeting, userId);
        FcmTimer certificationTime = getFcmTimer(requestId);
        List<Participation> participations = meeting.getParticipations();
        List<ParticipationDetailResponseDto> participationDetails = ParticipationDetailResponseDto.listOf(participations);
        LocalDateTime meetingTime = getMeetingTime(meeting.getDate(), meeting.getTime());
        return MeetingDetailResponseDto.of(meeting, meetingTime, certificationTime.getStartTime(), participationDetails);
    }

    /**
     * throw: 방장이 아닌 경우
     * throw: 날짜 & 시간 수정 -> 모임 참가 인원중 2시간 이내 만남이 있는 경우
     * throw: 날짜 & 시간 수정 -> 모임 시작이 2시간 이내일 경우.
     * throw: 모임이 확정된 경우.
     */
    public UpdateMeetingInfoResponseDto updateMeetingInfo(Long userId, UpdateMeetingInfoRequestDto requestDto) {
        Meeting meeting = getMeeting(requestDto.getMeetingId());
        validateMeetingHost(meeting.getId(), userId);
        validateMeetingStatus(meeting);
        validateMeetingDateTime(meeting, requestDto.getDate(), requestDto.getTime());
        meeting.updateTitleAndPlaceAndDateAndTimeAndMemo(requestDto.getTitle(), requestDto.getPlace(), requestDto.getDate(), requestDto.getTime(), requestDto.getMemo());
        return UpdateMeetingInfoResponseDto.of(meeting.getId(), meeting.getTitle(), meeting.getPlace(), meeting.getDate(), meeting.getTime(), meeting.getMemo());
    }

    private Meeting getMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
    }

    private LocalDateTime getMeetingTime(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time);
    }

    /**
     * 서버 시간 기준 a ~ b분 사이에 모임을 조회하는 메서드입니다.
     * using:
     * NotificationScheduler - 1시간 전 모임 조회
     */
    public List<Meeting> findMeetingsInRange(LocalDateTime localDateTime, int from, int to) {
        LocalDateTime fromDateTime = localDateTime.plusMinutes(from);
        LocalDateTime toDateTime = localDateTime.plusMinutes(to);
        return meetingRepository.findMeetingsStartingSoon(
                fromDateTime.toLocalTime(),
                toDateTime.toLocalDate(),
                toDateTime.toLocalTime());
    }

    private List<Meeting> findMeetingsInRangeForUser(Long userId, LocalDateTime localDateTime, int from, int to) {
        LocalDateTime fromDateTime = localDateTime.plusMinutes(from);
        LocalDateTime toDateTime = localDateTime.plusMinutes(to);
        return meetingRepository.findMeetingsWithinTimeRange(
                userId,
                fromDateTime,
                toDateTime);
    }

    private Long getTimeUntilMeeting(Meeting meeting) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime meetingTime = LocalDateTime.of(meeting.getDate(), meeting.getTime());
        Duration duration = Duration.between(now, meetingTime);
        return duration.toSeconds();
    }

    private void validateParticipation(Meeting meeting, Long userId) {
        boolean isValidParticipation = meeting.getParticipations().stream()
                .anyMatch(participation -> participation.getUser().getId() == userId);
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
            throw new ForbiddenException(INVALID_MODIFY_TIME);
    }

    private void validateMeetingDateTime(Meeting meeting, LocalDate requestDate, LocalTime requestTime) {
        if (requestDate == null && requestTime == null) return;
        LocalDate date = (requestDate == null) ? meeting.getDate() : requestDate;
        LocalTime time = (requestTime == null) ? meeting.getTime() : requestTime;
        validateModifyTimeWithRemainTime(meeting);
        validateMeetingTime(meeting, date, time);
    }

    private void validateModifyTimeWithRemainTime(Meeting meeting) {
        if (getTimeUntilMeeting(meeting) <= 7200)
            throw new ForbiddenException(INVALID_MODIFY_TIME);
    }

    private void validateMeetingTime(Meeting meeting, LocalDate date, LocalTime time) {
        LocalDateTime meetingTime = LocalDateTime.of(date, time);
        for (Participation participation : meeting.getParticipations()) {
            isMeetingInDeadline(meeting.getId(), participation.getUser().getId(), meetingTime);
        }
    }

    public void isMeetingInDeadline(Long meetingId, Long userId, LocalDateTime meetingTime) {
        List<Meeting> meetings = findMeetingsInRangeForUser(userId, meetingTime, -120, 120)
                .stream()
                .filter(m -> m.getId() != meetingId)
                .toList();
        if (!meetings.isEmpty())
            throw new InvalidValueException(UNAVAILABLE_MEETING_TIME);
    }

    private FcmTimer getFcmTimer(Long fcmTimerId) {
        return fcmTimerRepository.findById(fcmTimerId)
                .orElse(FcmTimer.createFcmTimerWithNull());
    }
}
