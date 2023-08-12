package org.baggle.domain.meeting.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class UpdateMeetingInfoRequestDto {
    private Long meetingId;
    private String title;
    private String place;
    private LocalDate date;
    private LocalTime time;
    private String memo;
}
