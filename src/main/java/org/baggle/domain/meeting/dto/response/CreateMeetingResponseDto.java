package org.baggle.domain.meeting.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CreateMeetingResponseDto {
    private Long meetingId;

    public static CreateMeetingResponseDto of(Long meetingId) {
        return new CreateMeetingResponseDto(meetingId);
    }
}
