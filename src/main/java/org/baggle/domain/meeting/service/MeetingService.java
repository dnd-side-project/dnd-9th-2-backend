package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.MeetingStatus;
import org.baggle.domain.meeting.domain.Period;
import org.baggle.domain.meeting.dto.request.CreateMeetingRequestDto;
import org.baggle.domain.meeting.dto.response.CreateMeetingResponseDto;
import org.baggle.domain.meeting.dto.response.GetMeetingsResponseDto;
import org.baggle.domain.meeting.dto.response.MeetingResponseDto;
import org.baggle.domain.meeting.repository.MeetingCountQueryDto;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.domain.user.domain.User;
import org.baggle.domain.user.repository.UserRepository;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.baggle.domain.meeting.domain.Period.getEnumPeriodFromStringPeriod;
import static org.baggle.global.common.TimeConverter.convertToLocalDate;
import static org.baggle.global.common.TimeConverter.convertToLocalTime;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MeetingService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    @Transactional
    public CreateMeetingResponseDto createMeeting(Long userId, CreateMeetingRequestDto createMeetingRequestDto) {
        LocalDateTime meetingTime = createMeetingRequestDto.getMeetingTime();
        validateValidMeetingTime(meetingTime);
        validateAvailableMeetingTime(userId, meetingTime);
        User findUser = getUser(userId);
        Meeting meeting = createMeeting(findUser, createMeetingRequestDto);
        Meeting savedMeeting = meetingRepository.save(meeting);
        return CreateMeetingResponseDto.of(savedMeeting.getId());
    }

    public GetMeetingsResponseDto getMeetings(Long userId, String period, Pageable pageable) {
        Period enumPeriod = getEnumPeriodFromStringPeriod(period);
        MeetingCountQueryDto meetingCountQueryDto = getMeetingCount(userId);
        List<MeetingResponseDto> meetingResponseDtos = getMeetingsAccordingToPeriod(userId, enumPeriod, pageable);
        return GetMeetingsResponseDto.of(meetingCountQueryDto, meetingResponseDtos);
    }

    private void validateValidMeetingTime(LocalDateTime meetingTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime criteriaTime = now.plusHours(1L);
        if (criteriaTime.isAfter(meetingTime)) {
            throw new InvalidValueException(ErrorCode.INVALID_MEETING_TIME);
        }
    }

    private void validateAvailableMeetingTime(Long userId, LocalDateTime meetingTime) {
        List<Meeting> findMeetings = meetingRepository.findMeetingsWithinTimeRange(userId, meetingTime.minusHours(1L), meetingTime.plusHours(1L));
        if (!findMeetings.isEmpty()) {
            throw new InvalidValueException(ErrorCode.UNAVAILABLE_MEETING_TIME);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private Meeting createMeeting(User user, CreateMeetingRequestDto createMeetingRequestDto) {
        return Meeting.createMeeting(user, createMeetingRequestDto.getTitle(), createMeetingRequestDto.getPlace(), convertToLocalDate(createMeetingRequestDto.getMeetingTime()),
                convertToLocalTime(createMeetingRequestDto.getMeetingTime()), createMeetingRequestDto.getMemo());
    }

    private MeetingCountQueryDto getMeetingCount(Long userId) {
        return meetingRepository.countMeetings(MeetingStatus.PAST, userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEETING_COUNT_NOT_FOUND));
    }

    private List<MeetingResponseDto> getMeetingsAccordingToPeriod(Long userId, Period period, Pageable pageable) {
        Page<Meeting> meetings;
        LocalDateTime now = LocalDateTime.now();
        if (period == Period.SCHEDULED) {
            meetings = meetingRepository.findMeetingsWithoutMeetingStatus(userId, MeetingStatus.PAST, convertToLocalDate(now).toString(), convertToLocalTime(now).toString(), pageable);
        } else {
            meetings = meetingRepository.findMeetingsWithMeetingStatus(userId, MeetingStatus.PAST, convertToLocalDate(now).toString(), convertToLocalTime(now).toString(), pageable);
        }
        return meetings.stream()
                .map(MeetingResponseDto::of)
                .toList();
    }
}
