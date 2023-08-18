package org.baggle.domain.meeting.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.meeting.repository.MeetingCountQueryDto;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class GetMeetingResponseDto {
    private Long scheduledCount;
    private Long pastCount;
    private List<MeetingResponseDto> meetings;

    public static GetMeetingResponseDto of(MeetingCountQueryDto meetingCountQueryDto, List<MeetingResponseDto> meetings) {
        return GetMeetingResponseDto.builder()
                .scheduledCount(meetingCountQueryDto.getScheduledCount() != null ? meetingCountQueryDto.getScheduledCount() : 0)
                .pastCount(meetingCountQueryDto.getPastCount() != null ? meetingCountQueryDto.getPastCount() : 0)
                .meetings(meetings)
                .build();
    }
}
