package org.baggle.domain.fcm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Builder
@Getter
@RedisHash(value = "fcmNotification", timeToLive = 5)
public class FcmNotification {
    @Id
    private Long id;
    private Boolean isNotified;
}
