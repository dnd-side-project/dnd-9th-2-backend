package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.dto.request.CreateMeetingRequestDto;
import org.baggle.domain.meeting.dto.response.CreateMeetingResponseDto;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.domain.user.domain.User;
import org.baggle.domain.user.repository.UserRepository;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.ForbiddenException;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class MeetingService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    public CreateMeetingResponseDto createMeeting(Long userId, CreateMeetingRequestDto createMeetingRequestDto) {
        User findUser = getUser(userId);
        validateCreateMeeting(findUser.getId(), createMeetingRequestDto.getMeetingTime());
        Meeting meeting = createMeetingAndParticipationWithUser(findUser, createMeetingRequestDto);
        Meeting savedMeeting = meetingRepository.save(meeting);
        return CreateMeetingResponseDto.of(savedMeeting.getId());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateCreateMeeting(Long userId, LocalDateTime meetingTime) {
        validateAvailableMeetingTime(userId, meetingTime);
        LocalDate meetingDate = convertLocalDateTimeToLocalDate(meetingTime);
        validateMaximumCreatableMeetings(userId, meetingDate);
    }

    private Meeting createMeetingAndParticipationWithUser(User user, CreateMeetingRequestDto createMeetingRequestDto) {
        return Meeting.createMeeting(user, createMeetingRequestDto.getTitle(), createMeetingRequestDto.getPlace(),
                convertLocalDateTimeToLocalDate(createMeetingRequestDto.getMeetingTime()),
                convertLocalDateTimeToLocalTime(createMeetingRequestDto.getMeetingTime()),
                createMeetingRequestDto.getMemo());
    }

    private void validateAvailableMeetingTime(Long userId, LocalDateTime meetingTime) {
        LocalDateTime prevMeetingTime = meetingTime.minusHours(2L);
        LocalDateTime nextMeetingTime = meetingTime.plusHours(2L);
        List<Meeting> findMeetings = meetingRepository.findMeetingsWithinTimeRange(userId, prevMeetingTime, nextMeetingTime);
        if (!findMeetings.isEmpty()) {
            throw new InvalidValueException(ErrorCode.UNAVAILABLE_MEETING_TIME);
        }
    }

    private void validateMaximumCreatableMeetings(Long userId, LocalDate meetingDate) {
        Long meetingCount = meetingRepository.countMeetingWithinDate(userId, meetingDate);
        if (meetingCount > 1) {
            throw new ForbiddenException(ErrorCode.INVALID_CREATE_MEETING);
        }
    }

    private LocalDate convertLocalDateTimeToLocalDate(LocalDateTime meetingTime) {
        return meetingTime.toLocalDate();
    }

    private LocalTime convertLocalDateTimeToLocalTime(LocalDateTime meetingTime) {
        return meetingTime.toLocalTime();
    }
}
