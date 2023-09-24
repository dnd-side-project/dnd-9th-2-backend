package org.baggle.domain.user.auth.kakao;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoOAuthProvider {
    private final KakaoFeignClient kakaoFeignClient;

    public String getKakaoPlatformId(String accessToken) {
        KakaoAccessToken kakaoAccessToken = KakaoAccessToken.createKakaoAccessToken(accessToken);
        String accessTokenWithTokenType = kakaoAccessToken.getAccessTokenWithTokenType();
        KakaoAccessTokenInfo kakaoAccessTokenInfo = getKakaoAccessTokenInfo(accessTokenWithTokenType);
        return String.valueOf(kakaoAccessTokenInfo.getId());
    }

    private KakaoAccessTokenInfo getKakaoAccessTokenInfo(String accessTokenWithTokenType) {
        try {
            return kakaoFeignClient.getKakaoAccessTokenInfo(accessTokenWithTokenType);
        } catch (FeignException e) {
            log.error("Feign Exception: ", e);
            throw new UnauthorizedException(ErrorCode.INVALID_KAKAO_ACCESS_TOKEN);
        }
    }
}
