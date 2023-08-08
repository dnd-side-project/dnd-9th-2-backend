package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.domain.*;
import org.baggle.domain.meeting.dto.reponse.ParticipationResponseDto;
import org.baggle.domain.meeting.dto.reponse.ParticipationAvailabilityResponseDto;
import org.baggle.domain.meeting.dto.request.ParticipationReqeustDto;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.domain.user.domain.User;
import org.baggle.domain.user.repository.UserRepository;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.baggle.global.error.exception.ErrorCode.MEETING_NOT_FOUNT;

@RequiredArgsConstructor
@Transactional
@Service
public class ParticipationService {
    private final MeetingRepository meetingRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;

    public ParticipationAvailabilityResponseDto findParticipationAvailability(Long requestId){
        Meeting meeting = meetingRepository.findById(requestId).orElseThrow(()-> new EntityNotFoundException(MEETING_NOT_FOUNT));
        List<Participation> participations = meeting.getParticipations();
        if(hasUser(participations, requestId)) return null;
        return ParticipationAvailabilityResponseDto.of(meeting);
    }

    /**
     * TODO 모임 앞/뒤 2시간 내에 일정있는 지 확인
     */
    public ParticipationResponseDto createParticipation(User user, ParticipationReqeustDto reqeustDto){
        Meeting meeting = meetingRepository.findById(reqeustDto.getMeetingId()).orElseThrow(()-> new EntityNotFoundException(MEETING_NOT_FOUNT));
        Participation participation = reqeustDto.toEntity(user, meeting, MeetingAuthority.PARTICIPATION, ParticipationMeetingStatus.PARTICIPATING, ButtonAuthority.NON_OWNER);
        participationRepository.save(participation);
        return ParticipationResponseDto.of(participation.getId());
    }

    private Boolean hasUser(List<Participation> participations, Long userId){
        return participations.stream()
                .anyMatch(participation -> Objects.equals(participation.getUser().getId(), userId));
    }

}
