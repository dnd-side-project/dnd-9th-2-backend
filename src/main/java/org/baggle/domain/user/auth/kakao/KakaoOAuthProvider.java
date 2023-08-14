package org.baggle.domain.user.auth.kakao;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoOAuthProvider {
    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.client-secret}")
    private String clientSecret;
    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;
    private final KakaoAccessTokenFeignClient kakaoAccessTokenFeignClient;
    private final KakaoAccessTokenInfoFeignClient kakaoAccessTokenInfoFeignClient;

    public String getKakaoPlatformId(String authorizationCode) {
        KakaoAccessToken kakaoAccessToken = getKakaoAccessToken(clientId, redirectUri, authorizationCode, clientSecret);
        String accessTokenWithTokenType = kakaoAccessToken.getAccessTokenWithTokenType();
        KakaoAccessTokenInfo kakaoAccessTokenInfo = kakaoAccessTokenInfoFeignClient.getKakaoAccessTokenInfo(accessTokenWithTokenType);
        return String.valueOf(kakaoAccessTokenInfo.getId());
    }

    private KakaoAccessToken getKakaoAccessToken(String clientId, String redirectUri, String code, String clientSecret) {
        try {
            log.info("feign request: {}, {}, {}, {}", clientId, redirectUri, code, clientSecret);
            return kakaoAccessTokenFeignClient.getKakaoAccessToken("authorization_code", clientId, redirectUri, code, clientSecret);
        } catch (FeignException e) {
            log.info("feign exception: ", e);
            throw new UnauthorizedException(ErrorCode.INVALID_AUTHORIZATION_CODE);
        }
    }
}
