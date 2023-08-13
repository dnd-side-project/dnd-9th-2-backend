package org.baggle.domain.user.auth.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakao-access-token-info-feign-client", url = "https://kapi.kakao.com/v1/user/access_token_info")
public interface KakaoAccessTokenInfoFeignClient {
    @GetMapping
    KakaoAccessTokenInfo getKakaoAccessTokenInfo(@RequestHeader("Authorization") String accessToken);
}
