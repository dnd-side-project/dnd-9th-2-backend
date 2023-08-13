package org.baggle.domain.meeting.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UpdateMeetingInfoRequestDto {
    private Long meetingId;
    private String title;
    private String place;
    private LocalDate date;
    private LocalTime time;
    private String memo;
}
