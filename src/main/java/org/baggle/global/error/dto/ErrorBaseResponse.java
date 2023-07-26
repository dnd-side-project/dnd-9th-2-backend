package org.baggle.global.error.dto;

import lombok.Builder;
import lombok.Getter;
import org.baggle.global.error.exception.ErrorCode;

@Getter
public class ErrorBaseResponse {
    private int status;
    private String message;

    @Builder
    public ErrorBaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorBaseResponse of(ErrorCode errorCode) {
        return ErrorBaseResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();
    }
}
