package org.baggle.domain.fcm.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.fcm.domain.FcmToken;

import java.util.List;

@Builder
@Getter
public class FcmNotificationRequestDto {
    private List<FcmToken> targetTokenList;
    private String title;
    private String body;


    public static FcmNotificationRequestDto of(List<FcmToken> targetTokenList, String title, String body) {
        return FcmNotificationRequestDto.builder()
                .targetTokenList(targetTokenList)
                .title(title)
                .body(body)
                .build();
    }
}
