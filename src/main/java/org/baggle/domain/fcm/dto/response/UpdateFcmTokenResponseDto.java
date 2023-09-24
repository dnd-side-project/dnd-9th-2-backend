package org.baggle.domain.fcm.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.fcm.domain.FcmToken;

@Getter
public class UpdateFcmTokenResponseDto {
    private String fcmToken;

    @Builder
    public UpdateFcmTokenResponseDto(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public static UpdateFcmTokenResponseDto of(FcmToken fcmToken) {
        return UpdateFcmTokenResponseDto.builder()
                .fcmToken(fcmToken.getFcmToken())
                .build();
    }
}
