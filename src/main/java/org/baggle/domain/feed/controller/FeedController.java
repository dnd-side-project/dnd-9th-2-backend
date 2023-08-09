package org.baggle.domain.feed.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.feed.service.FeedService;
import org.baggle.domain.user.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/api/feed")
@Controller
public class FeedController {
    private final AuthService authService;
    private final FeedService feedService;

//    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<BaseResponse<?>> createFeedUpload(-----------------------,
//                                                            @RequestBody FeedUploadRequestDto requestDto,
//                                                            @RequestPart MultipartFile feedImage){
//        FeedUploadResponseDto responseDto = feedService.feedUpload(requestDto, feedImage);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
//    }

//    @GetMapping("/{member_id}")
//    public ResponseEntity<BaseResponse<?>> uploadNotification(________________,
//                                                              @PathVariable("member_id") Long memberId){
//
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
//    }

}
