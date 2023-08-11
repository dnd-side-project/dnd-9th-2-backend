package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmTimer;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.domain.meeting.dto.reponse.MeetingDetailResponseDto;
import org.baggle.domain.meeting.dto.reponse.ParticipationDetailResponseDto;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.baggle.global.error.exception.ErrorCode.MEETING_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final FcmTimerRepository fcmTimerRepository;


    /**
     * throw 모임이 존재하지 않는 경우
     */
    public MeetingDetailResponseDto findMeetingDetail(Long requestId) {
        Meeting meeting = meetingRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
        FcmTimer certificationTime = fcmTimerRepository.findById(requestId).orElse(new FcmTimer(null, null));
        List<Participation> participations = meeting.getParticipations();
        List<ParticipationDetailResponseDto> participationDetails = participations.stream().map(participation -> ParticipationDetailResponseDto.of(participation, participation.getUser(), participation.getFeed())).toList();
        return MeetingDetailResponseDto.of(meeting, certificationTime.getStartTime(), participationDetails);
    }


    /**
     * 서버 시간 기준 a ~ b분 사이에 모임을 조회하는 메서드입니다.
     * using:
     * NotificationScheduler - 1시간 전 모임 조회
     * ParticipationService - 모임 전 후 2시간이내 모임 체크
     */
    public List<Meeting> findMeetingsInRange(LocalDateTime localDateTime, int from, int to) {
        LocalDateTime fromDateTime = localDateTime.plusMinutes(from);
        LocalDateTime toDateTime = localDateTime.plusMinutes(to);

        return meetingRepository.findMeetingsStartingSoon(
                fromDateTime.toLocalTime(),
                toDateTime.toLocalDate(),
                toDateTime.toLocalTime());
    }

    /**
     * 2시간 전,후 모임 여부를 확인하는 메서드
     * return: 모임이 있을 경우 True, else False
     */
    public Boolean isMeetingInDeadline(Meeting meeting) {
        LocalDateTime criteriaTime = LocalDateTime.of(meeting.getDate(), meeting.getTime());
        List<Meeting> meetings = this.findMeetingsInRange(criteriaTime, -120, 120);
        return meetings.size() != 0;
    }

    /**
     * 모임 시간까지 남은 시간을 확인하는 메서드
     * return: 1시간 이상 True, else False
     */
    public Boolean isValidTime(Meeting meeting) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime meetingTime = LocalDateTime.of(meeting.getDate(), meeting.getTime());
        Duration duration = Duration.between(now, meetingTime);
        return duration.toMinutes() > 60;
    }
}
