package org.baggle.global.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {
    /**
     * 200 Ok
     */
    OK(HttpStatus.OK, "요청이 성공했습니다."),

    /**
     * 201 Created
     */
    CREATED(HttpStatus.CREATED, "요청이 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
