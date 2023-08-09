package org.baggle.domain.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.InvalidValueException;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Platform {
    KAKAO("kakao"),
    APPLE("apple"),
    WITHDRAW("withdraw");

    private final String stringPlatform;

    public static Platform getEnumPlatformFromStringPlatform(String stringPlatform) {
        return Arrays.stream(values())
                .filter(platform -> platform.stringPlatform.equals(stringPlatform))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.INVALID_PLATFORM_TYPE));
    }
}
