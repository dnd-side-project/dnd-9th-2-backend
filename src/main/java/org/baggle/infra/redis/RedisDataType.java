package org.baggle.infra.redis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.baggle.global.error.exception.InvalidValueException;

import java.util.Arrays;

import static org.baggle.global.error.exception.ErrorCode.INVALID_REDIS_DATA_TYPE;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum RedisDataType {
    REFRESHTOKEN("refreshToken"),
    FCM_NOTIFICATION("fcmNotification"),
    FCM_TIMER("fcmTimer");

    private final String stringPlatform;

    public static RedisDataType getEnumRedisDataTypeFromString(String stringRedisType) {
        return Arrays.stream(values())
                .filter(redisDataType -> redisDataType.stringPlatform.equals(stringRedisType))
                .findAny()
                .orElseThrow(() -> new InvalidValueException(INVALID_REDIS_DATA_TYPE));
    }
}
