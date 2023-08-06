package org.baggle.domain.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Builder
@Getter
@RedisHash(value = "refreshToken", timeToLive = 60)
public class RefreshToken {
    @Id
    private Long id;
    private String refreshToken;
}
