package org.baggle.domain.user.auth.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "apple-feign-client", url = "https://appleid.apple.com/auth")
public interface AppleFeignClient {
    @GetMapping("/keys")
    ApplePublicKeys getApplePublicKeys();
}
