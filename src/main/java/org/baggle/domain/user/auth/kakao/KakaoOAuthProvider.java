package org.baggle.domain.user.auth.kakao;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KakaoOAuthProvider {
    @Value("${oauth.kakao.client-id")
    private String clientId;
    @Value("${oauth.kakao.client-secret")
    private String clientSecret;
    @Value("${oauth.kakao.redirect-uri")
    private String redirectUri;
    private final KakaoAccessTokenFeignClient kakaoAccessTokenFeignClient;
    private final KakaoAccessTokenInfoFeignClient kakaoAccessTokenInfoFeignClient;

    public String getKakaoPlatformId(String authorizationCode) {
        KakaoAccessTokenRequestDto kakaoAccessTokenRequestDto = KakaoAccessTokenRequestDto.of(clientId, redirectUri, authorizationCode, clientSecret);
        KakaoAccessToken kakaoAccessToken = getKakaoAccessToken(kakaoAccessTokenRequestDto);
        String accessTokenWithTokenType = kakaoAccessToken.getAccessTokenWithTokenType();
        KakaoAccessTokenInfo kakaoAccessTokenInfo = kakaoAccessTokenInfoFeignClient.getKakaoAccessTokenInfo(accessTokenWithTokenType);
        return String.valueOf(kakaoAccessTokenInfo.getId());
    }

    private KakaoAccessToken getKakaoAccessToken(KakaoAccessTokenRequestDto kakaoAccessTokenRequestDto) {
        try {
            return kakaoAccessTokenFeignClient.getKakaoAccessToken(kakaoAccessTokenRequestDto);
        } catch (FeignException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_AUTHORIZATION_CODE);
        }
    }
}
