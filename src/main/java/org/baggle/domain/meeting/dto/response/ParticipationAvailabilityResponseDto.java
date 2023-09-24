package org.baggle.domain.meeting.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.meeting.domain.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ParticipationAvailabilityResponseDto {
    private Long meetingId;
    private String title;
    private String place;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    private LocalTime time;
    private String memo;

    @Builder
    public ParticipationAvailabilityResponseDto(Long meetingId, String title, String place, LocalDate date, LocalTime time, String memo) {
        this.meetingId = meetingId;
        this.title = title;
        this.place = place;
        this.date = date;
        this.time = time;
        this.memo = memo;
    }

    public static ParticipationAvailabilityResponseDto of(Meeting meeting) {
        return ParticipationAvailabilityResponseDto.builder()
                .meetingId(meeting.getId())
                .title(meeting.getTitle())
                .place(meeting.getPlace())
                .date(meeting.getDate())
                .time(meeting.getTime())
                .memo(meeting.getMemo())
                .build();
    }

}

