package org.baggle.domain.fcm.dto.request;

import lombok.Getter;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.user.domain.User;

@Getter
public class UpdateFcmTokenRequestDto {
    private String beforeFCMToken;
    private String updateFCMToken;

}
