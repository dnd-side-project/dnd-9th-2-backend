package org.baggle.domain.user.auth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class KakaoAccessTokenRequestDto {
    @JsonProperty("grant_type")
    private String grantType;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("redirect_uri")
    private String redirectUri;
    private String code;
    @JsonProperty("client_secret")
    private String clientSecret;

    public static KakaoAccessTokenRequestDto of(String clientId, String redirectUri, String code, String clientSecret) {
        return KakaoAccessTokenRequestDto.builder()
                .grantType("authorization_code")
                .clientId(clientId)
                .redirectUri(redirectUri)
                .code(code)
                .clientSecret(clientSecret)
                .build();
    }
}
