package org.baggle.domain.fcm.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.fcm.domain.FcmToken;

@Getter
public class GetFcmTokenResponseDto {
    private String fcmToken;

    @Builder
    public GetFcmTokenResponseDto(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public static GetFcmTokenResponseDto of(FcmToken fcmToken) {
        return GetFcmTokenResponseDto.builder()
                .fcmToken(fcmToken.getFcmToken())
                .build();
    }
}
