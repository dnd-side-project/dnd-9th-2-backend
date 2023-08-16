package org.baggle.domain.feed.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FeedUploadResponseDto {
    private Long feedId;
    private String feedUrl;

    public static FeedUploadResponseDto of(Long feedId, String feedUrl) {
        return FeedUploadResponseDto.builder()
                .feedId(feedId)
                .feedUrl(feedUrl)
                .build();
    }
}
