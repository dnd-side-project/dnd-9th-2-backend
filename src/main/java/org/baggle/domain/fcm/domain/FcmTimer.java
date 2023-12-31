package org.baggle.domain.fcm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@RedisHash(value = "fcmTimer", timeToLive = 300)
public class FcmTimer {
    @Id
    private Long id;
    private LocalDateTime startTime;

    public static FcmTimer createFcmTimer(Long id, LocalDateTime startTime){
        return FcmTimer.builder()
                .id(id)
                .startTime(startTime)
                .build();
    }

    public static FcmTimer createFcmTimerWithNull() {
        return FcmTimer.builder()
                .id(null)
                .startTime(null)
                .build();
    }
}
