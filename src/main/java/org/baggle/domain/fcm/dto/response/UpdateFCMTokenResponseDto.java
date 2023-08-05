package org.baggle.domain.fcm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateFCMTokenResponseDto {
    private String fcmToken;
}
