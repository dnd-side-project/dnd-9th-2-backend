package org.baggle.domain.fcm.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.baggle.domain.fcm.domain.FcmToken;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmNotificationRequestDto {
    private List<FcmToken> targetUserIdList;
    private String title;
    private String body;

    @Builder
    public FcmNotificationRequestDto(List<FcmToken> targetUserIdList, String title, String body) {
        this.targetUserIdList = targetUserIdList;
        this.title = title;
        this.body = body;
    }

    public static FcmNotificationRequestDto of(List<FcmToken> targetUserIdList, String title, String body) {
        return FcmNotificationRequestDto.builder()
                .targetUserIdList(targetUserIdList)
                .title(title)
                .body(body)
                .build();
    }
}
