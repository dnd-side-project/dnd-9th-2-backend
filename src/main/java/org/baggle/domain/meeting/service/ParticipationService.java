package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.domain.*;

import org.baggle.domain.meeting.dto.request.ParticipationReqeustDto;
import org.baggle.domain.meeting.dto.response.ParticipationAvailabilityResponseDto;
import org.baggle.domain.meeting.dto.response.ParticipationResponseDto;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.domain.user.domain.User;
import org.baggle.domain.user.repository.UserRepository;
import org.baggle.global.error.exception.ConflictException;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.ForbiddenException;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.baggle.global.error.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional
@Service
public class ParticipationService {
    private final MeetingRepository meetingRepository;
    private final ParticipationRepository participationRepository;
    private final MeetingService meetingService;
    private final UserRepository userRepository;

    /**
     * 현재 모임에 참여 여부를 판단하는 메서드.
     * throw: 모임이 존재하지 않을 경우
     * throw: 2시간 내 모임이 존재하는 경우 or 모임시작 시간 < 1시간
     * throw: 이미 모임에 참여한 경우
     * throw: 모임이 다 찼을 경우
     */
    public ParticipationAvailabilityResponseDto findParticipationAvailability(Long userId, Long requestId) {
        Meeting meeting = meetingRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
        List<Participation> participations = meeting.getParticipations();
        validateMeetingTime(userId, meeting);
        duplicateParticipation(meeting.getParticipations(), userId);
        validateMeetingCapacity(meeting);
        return ParticipationAvailabilityResponseDto.of(meeting);
    }

    /**
     * 모임 참여 메서드
     */
    public ParticipationResponseDto createParticipation(Long userId, ParticipationReqeustDto reqeustDto) {
        Meeting meeting = getMeeting(reqeustDto.getMeetingId());
        User user = getUser(userId);
        Participation participation = Participation.createParticipationWithoutFeed(user, meeting, MeetingAuthority.PARTICIPATION, ParticipationMeetingStatus.PARTICIPATING, ButtonAuthority.NON_OWNER);
        participationRepository.save(participation);
        return ParticipationResponseDto.of(participation.getId());
    }

    private Meeting getMeeting(Long meetingId){
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
    }
    private User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
    }
    private void validateMeetingTime(Long userId, Meeting meeting){
        if (meetingService.isMeetingInDeadline(userId, meeting) || meeting.getMeetingStatus() != MeetingStatus.SCHEDULED)
            throw new InvalidValueException(INVALID_MEETING_TIME);
    }
    private void duplicateParticipation(List<Participation> participations, Long userId) {
        boolean isDupilcate = participations.stream()
                .anyMatch(participation ->
                        Objects.equals(participation.getUser().getId(), userId));
        if (isDupilcate)
            throw new ConflictException(DUPLICATE_PARTICIPATION);
    }
    private void validateMeetingCapacity(Meeting meeting) {
        if (meeting.getParticipations().size() == 6)
            throw new ForbiddenException(INVALID_MEETING_CAPACITY);
    }


}
