package org.baggle.domain.user.auth.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "kakao-access-token-feign-client", url = "https://kauth.kakao.com/oauth/token")
public interface KakaoAccessTokenFeignClient {
    @PostMapping(consumes = "application/x-www-form-urlencoded")
    KakaoAccessToken getKakaoAccessToken(KakaoAccessTokenRequestDto kakaoAccessTokenRequestDto);
}
