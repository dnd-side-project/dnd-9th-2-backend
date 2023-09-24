package org.baggle.domain.fcm.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.user.domain.User;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AddFcmTokenRequestDto {
    private String fcmToken;

    public FcmToken toEntity(User user) {
        return FcmToken.builder()
                .fcmToken(fcmToken)
                .user(user)
                .build();
    }
}
