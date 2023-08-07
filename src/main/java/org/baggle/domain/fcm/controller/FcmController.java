package org.baggle.domain.fcm.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.service.FcmService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/fcm")
public class FcmController {
    private final FcmService fcmService;

//    @GetMapping()
//    public ResponseEntity<GetFCMTokenResponseDto> findFcmTokens(--------------){
//        GetFCMTokenResponseDto responseDto = fcmService.findFcmTokens(-------------);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, responseDto));
//    }

//    @PostMapping()
//    public ResponseEntity<AddFCMTokenResponseDto> createFcmToken(header 부분,
//                                                              @RequestBody @Valid AddFCMTokenRequestDto requestDto){
//        AddFCMTokenResponseDto responseDto = fcmService.createFcmToken(requestDto, header에서 유저 가져오기);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, responseDto));
//    }
//    @PatchMapping()
//    public ResponseEntity<UpdateFCMTokenResponseDto> updateFcmToken(------------------,
//                                                                     @RequestBody @Valid UpdateFCMTokenRequestDto requestDto){
//        UpdateFCMTokenResponseDto responseDto = fcmService.updateFcmToken(requestDto);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, responseDto));
//    }

}
