package org.baggle.domain.sample.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.sample.dto.request.SampleRequestDto;
import org.baggle.domain.sample.service.SampleService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.baggle.global.config.jwt.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RequiredArgsConstructor
@Controller
public class SampleApiController {
    private final SampleService sampleService;

    @PostMapping("/sample")
    public ResponseEntity<BaseResponse<?>> createSample(
            @RequestHeader("Authorization") String token,
            @RequestBody final SampleRequestDto sampleRequestDto) {
        final Token tokenDto = sampleService.createSample(token, sampleRequestDto);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, tokenDto));
    }
}