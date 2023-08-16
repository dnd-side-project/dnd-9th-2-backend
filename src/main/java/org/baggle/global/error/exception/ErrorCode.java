package org.baggle.global.error.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    /**
     * 400 Bad Request
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_MULTIPART_FILE(HttpStatus.BAD_REQUEST, "유효하지 않은 파일입니다."),
    INVALID_FILE_UPLOAD(HttpStatus.BAD_REQUEST, "s3 파일 업로드에 실패했습니다."),
    INVALID_PLATFORM_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 플랫폼 타입입니다."),
    INVALID_REDIS_DATA_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 레디스 테이터 타입입니다."),
    INVALID_FCM_UPLOAD(HttpStatus.BAD_REQUEST, "firebase 알람 전송에 실패했습니다."),
    INVALID_MEETING_TIME(HttpStatus.BAD_REQUEST, "유효하지 않은 모임 시간입니다."),
    INVALID_CERTIFICATION_TIME(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 시간입니다."),
    INVALID_MEETING_PARTICIPATION(HttpStatus.BAD_REQUEST, "잘못된 모임 참가자 입니다."),
    UNAVAILABLE_MEETING_TIME(HttpStatus.BAD_REQUEST, "모임 2시간 전후 일정이 존재합니다."),

    /**
     * 401 Unauthorized
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "리소스 접근 권한이 없습니다."),
    INVALID_KAKAO_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "카카오 액세스 토큰의 정보를 조회하는 과정에서 오류가 발생하였습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "액세스 토큰의 형식이 올바르지 않습니다. Bearer 타입을 확인해 주세요."),
    INVALID_ACCESS_TOKEN_VALUE(HttpStatus.UNAUTHORIZED, "액세스 토큰의 값이 올바르지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다. 재발급 받아주세요."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰의 형식이 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN_VALUE(HttpStatus.UNAUTHORIZED, "리프레시 토큰의 값이 올바르지 않습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요."),
    NOT_MATCH_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "일치하지 않는 리프레시 토큰입니다."),
    INVALID_IDENTITY_TOKEN(HttpStatus.UNAUTHORIZED, "애플 아이덴터티 토큰의 형식이 올바르지 않습니다."),
    INVALID_IDENTITY_TOKEN_VALUE(HttpStatus.UNAUTHORIZED, "애플 아이덴터티 토큰의 값이 올바르지 않습니다."),
    EXPIRED_IDENTITY_TOKEN(HttpStatus.UNAUTHORIZED, "애플 아이덴터티 토큰의 유효 기간이 만료되었습니다."),
    INVALID_IDENTITY_TOKEN_CLAIMS(HttpStatus.UNAUTHORIZED, "애플 아이덴터티 토큰의 클레임 값이 올바르지 않습니다."),
    UNABLE_TO_CREATE_APPLE_PUBLIC_KEY(HttpStatus.UNAUTHORIZED, "애플 로그인 중 퍼블릭 키 생성에 문제가 발생했습니다."),

    /**
     * 403 Forbidden
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, "리소스 접근 권한이 없습니다."),
    INVALID_MEETING_AUTHORITY(HttpStatus.FORBIDDEN, "올바른 미팅 권한자가 아닙니다."),
    INVALID_MODIFY_TIME(HttpStatus.FORBIDDEN, "모임 정보 수정 가능한 시간이 아닙니다."),
    INVALID_MEETING_CAPACITY(HttpStatus.FORBIDDEN, "모임 인원이 초과됐습니다."),
    NOT_MATCH_BUTTON_OWNER(HttpStatus.FORBIDDEN, "긴급 버튼 할당자가 아닙니다."),

    /**
     * 404 Not Found
     */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "엔티티를 찾을 수 없습니다."),
    FCM_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "FCM 토큰을 찾을 수 없습니다."),
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "모임 정보를 찾을 수 없습니다."),
    PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND, "모임 참가자를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

    /**
     * 405 Method Not Allowed
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "잘못된 HTTP method 요청입니다."),

    /**
     * 409 Conflict
     */
    CONFLICT(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 회원입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    DUPLICATE_PARTICIPATION(HttpStatus.CONFLICT, "이미 존재하는 참가자입니다."),
    DUPLICATE_FEED(HttpStatus.CONFLICT, "이미 피드인증을 완료했습니다."),

    /**
     * 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
