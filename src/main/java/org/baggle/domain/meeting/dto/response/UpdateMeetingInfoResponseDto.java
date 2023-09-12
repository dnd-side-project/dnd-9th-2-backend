package org.baggle.domain.meeting.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UpdateMeetingInfoResponseDto {
    private Long meetingId;
    private String title;
    private String place;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime dateTime;
    private String memo;

    public static UpdateMeetingInfoResponseDto of(Long meetingId, String title, String place, LocalDateTime dateTime, String memo) {
        return UpdateMeetingInfoResponseDto.builder()
                .meetingId(meetingId)
                .title(title)
                .place(place)
                .dateTime(dateTime)
                .memo(memo)
                .build();
    }
}
