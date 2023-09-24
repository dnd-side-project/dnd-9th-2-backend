package org.baggle.domain.meeting.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UpdateMeetingInfoRequestDto {
    private Long meetingId;
    private String title;
    private String place;
    private LocalDateTime dateTime;
    private String memo;
}
