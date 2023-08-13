package org.baggle.domain.user.auth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class KakaoAccessToken {
    @JsonProperty("access_token")
    private String accessToken;

    public String getAccessTokenWithTokenType() {
        return "Bearer " + accessToken;
    }
}
