package org.baggle.domain.user.auth.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakao-access-token-feign-client", url = "https://kauth.kakao.com/oauth/token")
public interface KakaoAccessTokenFeignClient {
    @PostMapping
    KakaoAccessToken getKakaoAccessToken(@RequestParam("grant_type") String grantType,
                                         @RequestParam("client_id") String clientId,
                                         @RequestParam("redirect_uri") String redirectUri,
                                         @RequestParam("code") String code,
                                         @RequestParam("client_secret") String clientSecret);
}
