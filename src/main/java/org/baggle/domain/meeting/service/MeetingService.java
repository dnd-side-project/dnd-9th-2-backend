package org.baggle.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.MeetingStatus;
import org.baggle.domain.meeting.domain.Period;
import org.baggle.domain.meeting.dto.request.CreateMeetingRequestDto;
import org.baggle.domain.meeting.dto.response.CreateMeetingResponseDto;
import org.baggle.domain.meeting.dto.response.GetMeetingResponseDto;
import org.baggle.domain.meeting.dto.response.MeetingResponseDto;
import org.baggle.domain.meeting.repository.MeetingCountQueryDto;
import org.baggle.domain.meeting.repository.MeetingRepository;
import org.baggle.domain.user.domain.User;
import org.baggle.domain.user.repository.UserRepository;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.ForbiddenException;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        User findUser = getUser(userId);
        //validateAvailableMeetingTime(userId, createMeetingRequestDto.getMeetingTime());
        //validateMaximumCreatableMeetings(userId, convertToLocalDate(createMeetingRequestDto.getMeetingTime()));
        Meeting meeting = createMeeting(findUser, createMeetingRequestDto);
        Meeting savedMeeting = meetingRepository.save(meeting);
        return CreateMeetingResponseDto.of(savedMeeting.getId());
    }

    public GetMeetingResponseDto getMeetings(Long userId, String period, Pageable pageable) {
        Period enumPeriod = getEnumPeriodFromStringPeriod(period);
        MeetingCountQueryDto meetingCountQueryDto = getMeetingCount(userId);
        List<MeetingResponseDto> meetingResponseDtos = getMeetingsAccordingToPeriod(userId, enumPeriod, pageable);
        return GetMeetingResponseDto.of(meetingCountQueryDto, meetingResponseDtos);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateAvailableMeetingTime(Long userId, LocalDateTime meetingTime) {
        List<Meeting> findMeetings = meetingRepository.findMeetingsWithinTimeRange(userId, meetingTime.minusHours(2L), meetingTime.plusHours(2L));
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
        if (period == Period.SCHEDULED) {
            meetings = meetingRepository.findMeetingsWithoutMeetingStatus(userId, MeetingStatus.PAST, LocalDateTime.now(), pageable);
        } else {
            meetings = meetingRepository.findMeetingsWithMeetingStatus(userId, MeetingStatus.PAST, LocalDateTime.now(), pageable);
        }
        return meetings.stream()
                .map(MeetingResponseDto::of)
                .toList();
    }
}
