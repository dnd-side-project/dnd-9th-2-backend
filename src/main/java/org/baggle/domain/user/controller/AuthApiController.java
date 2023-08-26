package org.baggle.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.user.dto.request.UserSignInRequestDto;
import org.baggle.domain.user.dto.response.UserAuthResponseDto;
import org.baggle.domain.user.service.AuthService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.baggle.global.config.auth.UserId;
import org.baggle.global.config.jwt.Token;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@Controller
public class AuthApiController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<BaseResponse<?>> signIn(@RequestHeader("Authorization") final String token,
                                                  @RequestBody final UserSignInRequestDto userSignInRequestDto) {
        final UserAuthResponseDto userAuthResponseDto = authService.signIn(token, userSignInRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, userAuthResponseDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<?>> signUp(@RequestHeader("Authorization") final String token,
                                                  @RequestParam("file") final MultipartFile image,
                                                  @RequestParam final String nickname,
                                                  @RequestParam final String platform,
                                                  @RequestParam final String fcmToken) {
        final UserAuthResponseDto userAuthResponseDto = authService.signUp(token, image, nickname, platform, fcmToken);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.of(SuccessCode.CREATED, userAuthResponseDto));
    }

    @GetMapping("/reissue")
    public ResponseEntity<BaseResponse<?>> reissue(@RequestHeader("Authorization") final String refreshToken) {
        final Token reissuedToken = authService.reissue(refreshToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, reissuedToken));
    }

    @PatchMapping("/signout")
    public ResponseEntity<BaseResponse<?>> signOut(@UserId final Long userId) {
        authService.signOut(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, null));
    }

    @PatchMapping("/withdraw")
    public ResponseEntity<BaseResponse<?>> withdraw(@UserId final Long userId) {
        authService.withdraw(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, null));
    }
}