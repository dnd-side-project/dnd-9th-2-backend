package org.baggle.domain.fcm.dto.request;

import lombok.Getter;

@Getter
public class UpdateFcmTokenRequestDto {
    private String beforeFCMToken;
    private String updateFCMToken;

}
