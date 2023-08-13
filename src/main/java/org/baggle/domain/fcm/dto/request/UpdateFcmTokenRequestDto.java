package org.baggle.domain.fcm.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UpdateFcmTokenRequestDto {
    private String beforeFCMToken;
    private String updateFCMToken;

}
