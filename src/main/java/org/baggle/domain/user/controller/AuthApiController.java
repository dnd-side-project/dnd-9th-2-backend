package org.baggle.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.user.service.AuthService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
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
    public ResponseEntity<BaseResponse<?>> signIn(@RequestHeader("Authorization") final String token) {
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, authService.signin(token)));
    }

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<?>> signUp(@RequestHeader("Authorization") final String token,
                                                  @RequestParam("image") final MultipartFile image,
                                                  @RequestParam final String nickname,
                                                  @RequestParam final String platform) {
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, authService.signup(token, image, nickname, platform)));
    }

    @GetMapping("/reissue")
    public ResponseEntity<BaseResponse<?>> signUp(@RequestHeader("Authorization") final String refreshToken) {
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, authService.reissue(refreshToken)));
    }
}
