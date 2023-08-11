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
//    public ResponseEntity<BaseResponse<?>> createFeedUpload(@RequestPart final FeedUploadRequestDto uploadInfo,
//                                                            @RequestPart final MultipartFile feedImage) {
//        final FeedUploadResponseDto responseDto = feedService.feedUpload(uploadInfo, feedImage);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
//    }
//
//    @GetMapping()
//    public ResponseEntity<BaseResponse<?>> uploadNotification(@RequestParam final Long memberId,
//                                                              @RequestParam final LocalDateTime authorizationTime) {
//        final FeedNotificationResponseDto responseDto = feedService.uploadNotification(memberId, authorizationTime);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
//    }

}
