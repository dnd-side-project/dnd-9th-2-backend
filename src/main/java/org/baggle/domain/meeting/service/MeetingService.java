package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmNotification;
import org.baggle.domain.fcm.domain.FcmTimer;
import org.baggle.domain.fcm.repository.FcmTimerRepository;
import org.baggle.domain.feed.repository.FeedRepository;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.domain.meeting.dto.reponse.MeetingDetailResponseDto;
import org.baggle.domain.meeting.dto.reponse.ParticipationDetailResponseDto;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.baggle.global.error.exception.ErrorCode.MEETING_NOT_FOUNT;

@RequiredArgsConstructor
@Transactional
@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final FeedRepository feedRepository;
    private final FcmTimerRepository fcmTimerRepository;


    public MeetingDetailResponseDto findMeetingDetail(Long requestId) {
        Meeting meeting = meetingRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUNT));
        FcmTimer certificationTime = fcmTimerRepository.findById(requestId).orElse(new FcmTimer(null, null));
        List<Participation> participations = meeting.getParticipations();
        List<ParticipationDetailResponseDto> participationDetails = participations.stream()
                .map(participation ->
                        ParticipationDetailResponseDto.of(participation, participation.getUser(), participation.getFeed()))
                .toList();
        return MeetingDetailResponseDto.of(meeting, certificationTime.getStartTime(), participationDetails);
    }


    /**
     * 서버 시간 기준 a ~ b분 사이에 모임을 조회하는 메서드입니다.
     * using:
     * NotificationScheduler - 1시간 전 모임 조회
     */
    public List<Meeting> findMeetingsInRange(int from, int to) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDateTime = now.plusMinutes(from);
        LocalDateTime toDateTime = now.plusMinutes(to);

        return meetingRepository.findMeetingsStartingSoon(
                fromDateTime.toLocalTime(),
                toDateTime.toLocalDate(),
                toDateTime.toLocalTime());
    }

}
