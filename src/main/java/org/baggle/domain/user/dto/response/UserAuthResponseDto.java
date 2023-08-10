package org.baggle.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.user.domain.User;
import org.baggle.global.config.jwt.Token;

@Builder
@Getter
public class UserAuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String platform;
    private String profileImageUrl;
    private String nickname;

    public static UserAuthResponseDto of(Token token, User user) {
        return UserAuthResponseDto.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getAccessToken())
                .userId(user.getId())
                .platform(user.getPlatform().getStringPlatform())
                .profileImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .build();
    }
}
