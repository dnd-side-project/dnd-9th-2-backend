package org.baggle.global.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.baggle.global.error.dto.ErrorBaseResponse;
import org.baggle.global.error.exception.BusinessException;
import org.baggle.global.error.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Valid & Validated annotation의 binding error를 handling합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorBaseResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(">>> handle: MethodArgumentNotValidException ", e);
        final ErrorBaseResponse errorBaseResponse = ErrorBaseResponse.of(ErrorCode.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBaseResponse);
    }

    /**
     * ModelAttribute annotation의 binding error를 handling합니다.
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorBaseResponse> handleBindException(BindException e) {
        log.error(">>> handle: BindException ", e);
        final ErrorBaseResponse errorBaseResponse = ErrorBaseResponse.of(ErrorCode.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBaseResponse);
    }

    /**
     * RequestParam annotation의 binding error를 handling합니다.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorBaseResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error(">>> handle: MethodArgumentTypeMismatchException ", e);
        final ErrorBaseResponse errorBaseResponse = ErrorBaseResponse.of(ErrorCode.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBaseResponse);
    }

    /**
     * 지원하지 않는 HTTP method로 요청 시 발생하는 error를 handling합니다.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorBaseResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(">>> handle: HttpRequestMethodNotSupportedException ", e);
        final ErrorBaseResponse errorBaseResponse = ErrorBaseResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorBaseResponse);
    }

    /**
     * BusinessException을 handling합니다.
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorBaseResponse> handleBusinessException(final BusinessException e) {
        log.error(">>> handle: BusinessException ", e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorBaseResponse errorBaseResponse = ErrorBaseResponse.of(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorBaseResponse);
    }

    /**
     * 위에서 정의한 Exception을 제외한 모든 예외를 handling합니다.
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorBaseResponse> handleException(Exception e) {
        log.error(">>> handle: Exception ", e);
        final ErrorBaseResponse errorBaseResponse = ErrorBaseResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBaseResponse);
    }
}
