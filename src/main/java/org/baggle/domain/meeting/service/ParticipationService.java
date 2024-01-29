package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.domain.*;
import org.baggle.domain.meeting.dto.request.ParticipationRequestDto;
import org.baggle.domain.meeting.dto.response.ParticipationAvailabilityResponseDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.baggle.global.common.TimeConverter.convertToLocalDateTime;
import static org.baggle.global.error.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional
@Service
public class ParticipationService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final ParticipationRepository participationRepository;

    public ParticipationAvailabilityResponseDto findParticipationAvailability(Long userId, Long requestId) {
        Meeting meeting = findMeetingOrThrow(requestId);
        validateMeetingStatus(meeting);
        validateMeetingTime(userId, meeting);
        validateDuplicateParticipation(meeting, userId);
        validateMeetingCapacity(meeting);
        return ParticipationAvailabilityResponseDto.of(meeting);
    }

    public void createParticipation(Long userId, ParticipationRequestDto requestDto) {
        Meeting meeting = findMeetingOrThrow(requestDto.getMeetingId());
        User user = findUserOrThrow(userId);
        validateDuplicateParticipation(meeting, userId);
        validateMeetingStatus(meeting);
        validateMeetingCapacity(meeting);
        validateMeetingTime(userId, meeting);
        Participation participation = createParticipationWithRandomButtonAuthority(user, meeting);
        saveParticipation(participation);
    }

    public void delegateMeetingHost(Long fromMemberId, Long toMemberId) {
        Participation fromParticipation = findParticipationOrThrow(fromMemberId);
        Participation toParticipation = findParticipationOrThrow(toMemberId);
        validateMeetingHost(fromParticipation);
        validateMeetingStatus(fromParticipation.getMeeting());
        delegateMeetingHostToOtherParticipation(fromParticipation, toParticipation);
        withdrawBeforeMeetingConfirmation(fromParticipation, fromParticipation.getMeeting());
        updateButtonAuthorityWithRandomNumber(fromParticipation.getMeeting());
    }

    public void withdrawMember(Long memberId) {
        Participation participation = findParticipationOrThrow(memberId);
        validateMeetingStatus(participation.getMeeting());
        withdrawBeforeMeetingConfirmation(participation, participation.getMeeting());
        updateButtonAuthorityWithRandomNumber(participation.getMeeting());
    }

    private void delegateMeetingHostToOtherParticipation(Participation fromParticipation, Participation toParticipation) {
        fromParticipation.updateMeetingAuthorityToParticipation();
        toParticipation.updateMeetingAuthorityToHost();
    }

    private Participation createParticipationWithRandomButtonAuthority(User user, Meeting meeting) {
        Participation participation = Participation.createParticipationWithMeeting(user, meeting);
        updateButtonAuthorityWithRandomNumber(meeting);
        return participation;
    }

    private void updateButtonAuthorityWithRandomNumber(Meeting meeting) {
        int randomNumber = new Random().nextInt(meeting.getParticipations().size());
        meeting.initButtonAuthorityOfParticipationList();
        Participation randomNumberParticipation = meeting.getRandomNumberParticipation(randomNumber);
        randomNumberParticipation.updateButtonAuthority(ButtonAuthority.OWNER);
    }

    private void withdrawBeforeMeetingConfirmation(Participation participation, Meeting meeting) {
        meeting.withdrawParticipation(participation);
        participationRepository.delete(participation);
    }

    private void validateMeetingHost(Participation participation) {
        if (participation.getMeetingAuthority() != MeetingAuthority.HOST)
            throw new ForbiddenException(INVALID_MEETING_AUTHORITY);
    }

    private void validateMeetingStatus(Meeting meeting) {
        if (meeting.getMeetingStatus() != MeetingStatus.SCHEDULED)
            throw new InvalidValueException(INVALID_MEETING_TIME);
    }

    private void validateMeetingTime(Long userId, Meeting meeting) {
        List<Meeting> meetings = findMeetingsInRangeForUser(userId, convertToLocalDateTime(meeting.getDate(), meeting.getTime()), -60, 60)
                .stream()
                .filter(m -> !Objects.equals(m.getId(), meeting.getId()))
                .toList();
        if (!meetings.isEmpty())
            throw new InvalidValueException(UNAVAILABLE_MEETING_TIME);
    }

    private List<Meeting> findMeetingsInRangeForUser(Long userId, LocalDateTime localDateTime, int from, int to) {
        LocalDateTime fromDateTime = localDateTime.plusMinutes(from);
        LocalDateTime toDateTime = localDateTime.plusMinutes(to);
        return meetingRepository.findMeetingsWithinTimeRange(userId, fromDateTime, toDateTime);
    }

    private void validateDuplicateParticipation(Meeting meetingId, Long userId) {
        if (participationRepository.existsByMeetingIdAndUserId(meetingId.getId(), userId))
            throw new ConflictException(DUPLICATE_PARTICIPATION);
    }

    private void validateMeetingCapacity(Meeting meeting) {
        if (meeting.getParticipations().size() == 6)
            throw new ForbiddenException(INVALID_MEETING_CAPACITY);
    }

    private void saveParticipation(Participation participation) {
        participationRepository.save(participation);
    }

    private Meeting findMeetingOrThrow(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException(MEETING_NOT_FOUND));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    private Participation findParticipationOrThrow(Long participationId) {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException(PARTICIPATION_NOT_FOUND));
    }
}
