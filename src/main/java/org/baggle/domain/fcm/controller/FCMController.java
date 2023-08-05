package org.baggle.domain.fcm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.dto.request.AddFCMTokenRequestDto;
import org.baggle.domain.fcm.dto.request.UpdateFCMTokenRequestDto;
import org.baggle.domain.fcm.dto.response.AddFCMTokenResponseDto;
import org.baggle.domain.fcm.dto.response.GetFCMTokenResponseDto;
import org.baggle.domain.fcm.dto.response.UpdateFCMTokenResponseDto;
import org.baggle.domain.fcm.service.FCMService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/fcm")
public class FCMController {
    private final FCMService fcmService;

//    @GetMapping()
//    public ResponseEntity<GetFCMTokenResponseDto> getFcmTokens(--------------){
//        GetFCMTokenResponseDto responseDto = fcmService.getFcmTokens(-------------);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, responseDto));
//    }

//    @PostMapping()
//    public ResponseEntity<AddFCMTokenResponseDto> addFcmToken(header 부분,
//                                                              @RequestBody @Valid AddFCMTokenRequestDto requestDto){
//        AddFCMTokenResponseDto responseDto = fcmService.addFcmToken(requestDto, header에서 유저 가져오기);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, responseDto));
//    }
//    @PatchMapping()
//    public ResponseEntity<UpdateFCMTokenResponseDto> updateFcmToken(------------------,
//                                                                     @RequestBody @Valid UpdateFCMTokenRequestDto requestDto){
//        UpdateFCMTokenResponseDto responseDto = fcmService.updateFcmToken(requestDto);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, responseDto));
//    }

}
