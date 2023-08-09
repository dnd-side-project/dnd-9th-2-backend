package org.baggle.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImageType {
    PROFILE("profile"),
    FEED("feed");

    private final String imageType;
}
