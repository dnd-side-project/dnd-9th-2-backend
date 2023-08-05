package org.baggle.domain.fcm.dto.request;

import lombok.Getter;

@Getter
public class UpdateFCMTokenRequestDto {
    private String beforeFCMToken;
    private String updateFCMToken;
}
