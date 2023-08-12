package org.baggle.domain.meeting.dto.reponse;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
public class UpdateMeetingInfoResponseDto {
    private Long meetingId;
    private String title;
    private String place;
    private LocalDate date;
    private LocalTime time;
    private String memo;

    public static UpdateMeetingInfoResponseDto of(Long meetingId, String title, String place, LocalDate date, LocalTime time, String memo) {
        return UpdateMeetingInfoResponseDto.builder()
                .meetingId(meetingId)
                .title(title)
                .place(place)
                .date(date)
                .time(time)
                .memo(memo)
                .build();
    }
}
