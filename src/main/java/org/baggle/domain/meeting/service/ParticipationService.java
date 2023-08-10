package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.domain.*;
import org.baggle.domain.meeting.dto.reponse.ParticipationAvailabilityResponseDto;
import org.baggle.domain.meeting.dto.reponse.ParticipationResponseDto;
import org.baggle.domain.meeting.dto.request.ParticipationReqeustDto;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.domain.user.domain.User;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.baggle.global.error.exception.ErrorCode.INVALID_MEETING_TIME;
import static org.baggle.global.error.exception.ErrorCode.MEETING_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class ParticipationService {
    private final MeetingRepository meetingRepository;
    private final ParticipationRepository participationRepository;
    private final MeetingService meetingService;

    /**
     * 현재 모임에 참여 여부를 판단하는 메서드.
     * throw: 모임이 존재하지 않을 경우
     * throw: 2시간 내 모임이 존재하는 경우 or 모임시작 시간 < 1시간
     */
    public ParticipationAvailabilityResponseDto findParticipationAvailability(Long requestId) {
        Meeting meeting = meetingRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
        if(!meetingService.isMeetingInDeadline(meeting) || meetingService.isValidTime(meeting)) throw new InvalidValueException(INVALID_MEETING_TIME);
        List<Participation> participations = meeting.getParticipations();
        if (hasUser(participations, requestId)) return null;
        return ParticipationAvailabilityResponseDto.of(meeting);
    }

    /**
     * 모임 참여 메서드
     * throw: 모임이 존재하지 않을 경우
     * throw: 2시간 내 모임이 존재하는 경우 or 모임시작 시간 < 1시간
     */
    public ParticipationResponseDto createParticipation(User user, ParticipationReqeustDto reqeustDto) {
        Meeting meeting = meetingRepository.findById(reqeustDto.getMeetingId()).orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
        if(!meetingService.isMeetingInDeadline(meeting) || meetingService.isValidTime(meeting)) throw new InvalidValueException(INVALID_MEETING_TIME);
        Participation participation = reqeustDto.toEntity(user, meeting, MeetingAuthority.PARTICIPATION, ParticipationMeetingStatus.PARTICIPATING, ButtonAuthority.NON_OWNER);
        participationRepository.save(participation);
        return ParticipationResponseDto.of(participation.getId());
    }

    private Boolean hasUser(List<Participation> participations, Long userId) {
        return participations.stream()
                .anyMatch(participation -> Objects.equals(participation.getUser().getId(), userId));
    }



}
