package org.baggle.global.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;

    @Builder
    public BaseResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<?> of(SuccessCode successCode, T data) {
        return BaseResponse.builder()
                .status(successCode.getHttpStatus().value())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }
}
