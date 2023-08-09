package org.baggle.domain.feed.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.meeting.domain.Meeting;

import java.time.LocalDateTime;

@Builder
@Getter
public class FeedNotificationResponseDto {
    private Long meetingId;
    private LocalDateTime certificationTime;

    public static FeedNotificationResponseDto of(Meeting meeting, LocalDateTime certificationTime) {
        return FeedNotificationResponseDto.builder()
                .meetingId(meeting.getId())
                .certificationTime(certificationTime)
                .build();
    }
}
