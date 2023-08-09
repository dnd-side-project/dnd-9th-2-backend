package org.baggle.domain.feed.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FeedUploadResponseDto {
    private Long feedId;

    public static FeedUploadResponseDto of(Long feedId) {
        return FeedUploadResponseDto.builder()
                .feedId(feedId)
                .build();
    }
}
