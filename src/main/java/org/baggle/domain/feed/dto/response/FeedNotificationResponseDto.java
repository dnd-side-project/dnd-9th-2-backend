package org.baggle.domain.feed.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.meeting.domain.Meeting;

import java.time.LocalDateTime;

@Builder
@Getter
public class FeedNotificationResponseDto {
    private Long meetingId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime certificationTime;

    public static FeedNotificationResponseDto of(Meeting meeting, LocalDateTime certificationTime) {
        return FeedNotificationResponseDto.builder()
                .meetingId(meeting.getId())
                .certificationTime(certificationTime)
                .build();
    }
}
