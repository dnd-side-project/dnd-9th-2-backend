package org.baggle.domain.feed.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FeedUploadRequestDto {
    private Long participationId;
    private LocalDateTime authorizationTime;
}
