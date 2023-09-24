package org.baggle.domain.fcm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.fcm.service.FcmService;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Builder
@Getter
@RedisHash(value = "fcmNotification", timeToLive = 2820)
public class FcmNotification {
    @Id
    private Long id;
    private Boolean isNotified;

    public static FcmNotification createFcmNotification(Long key){
        return FcmNotification.builder()
                .id(key)
                .isNotified(Boolean.TRUE)
                .build();
    }
}
