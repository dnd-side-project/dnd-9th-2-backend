package org.baggle.global.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ImageType {
    PROFILE("profile"),
    FEED("feed");

    private final String imageType;
}
