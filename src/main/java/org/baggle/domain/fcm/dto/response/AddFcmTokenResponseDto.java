package org.baggle.domain.fcm.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.fcm.domain.FcmToken;

@Getter
public class AddFcmTokenResponseDto {
    private String fcmToken;

    @Builder
    public AddFcmTokenResponseDto(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public static AddFcmTokenResponseDto of(FcmToken fcmToken) {
        return AddFcmTokenResponseDto.builder()
                .fcmToken(fcmToken.getFcmToken())
                .build();
    }
}
