package org.baggle.domain.feed.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.feed.dto.request.FeedUploadRequestDto;
import org.baggle.domain.feed.dto.response.FeedNotificationResponseDto;
import org.baggle.domain.feed.dto.response.FeedUploadResponseDto;
import org.baggle.domain.feed.service.FeedService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api/feed")
@Controller
public class FeedApiController {
    private final FeedService feedService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BaseResponse<?>> createFeedUpload(@RequestPart final FeedUploadRequestDto memberInfo,
                                                            @RequestPart final MultipartFile feedImage) {
        final FeedUploadResponseDto responseDto = feedService.feedUpload(memberInfo, feedImage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.of(SuccessCode.CREATED, responseDto));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<?>> uploadNotification(@RequestParam final Long memberId,
                                                              @RequestParam final LocalDateTime authorizationTime) {
        final FeedNotificationResponseDto responseDto = feedService.uploadNotification(memberId, authorizationTime);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, responseDto));
    }
}