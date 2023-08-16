package org.baggle.domain.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.dto.response.MeetingDetailResponseDto;
import org.baggle.domain.meeting.dto.response.UpdateMeetingInfoResponseDto;
import org.baggle.domain.meeting.dto.request.UpdateMeetingInfoRequestDto;
import org.baggle.domain.meeting.service.MeetingDetailService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.baggle.global.config.auth.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/meeting")
@Controller
public class MeetingApiController {
    private final MeetingDetailService meetingDetailService;

    @GetMapping("/detail")
    public ResponseEntity<BaseResponse<?>> findMeetingDetail(@UserId final Long userId,
                                                             @RequestParam final Long meetingId) {
        MeetingDetailResponseDto responseDto = meetingDetailService.findMeetingDetail(userId, meetingId);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
    }

    @PatchMapping
    public ResponseEntity<BaseResponse<?>> updateMeetingInfo(@UserId final Long userId,
                                                             @RequestBody final UpdateMeetingInfoRequestDto requestDto) {
        final UpdateMeetingInfoResponseDto responseDto = meetingDetailService.updateMeetingInfo(userId, requestDto);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
    }


}
