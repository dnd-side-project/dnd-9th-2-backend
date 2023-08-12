package org.baggle.domain.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.dto.reponse.MeetingDetailResponseDto;
import org.baggle.domain.meeting.dto.reponse.UpdateMeetingInfoResponseDto;
import org.baggle.domain.meeting.dto.request.UpdateMeetingInfoRequestDto;
import org.baggle.domain.meeting.service.MeetingService;
import org.baggle.domain.user.service.AuthService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.baggle.global.config.auth.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/meeting")
@Controller
public class MeetingController {
    private final AuthService authService;
    private final MeetingService meetingService;

    @GetMapping("/detail")
    public ResponseEntity<BaseResponse<?>> findMeetingDetail(@UserId final Long userId,
                                                             @RequestParam final Long meetingId) {
        MeetingDetailResponseDto responseDto = meetingService.findMeetingDetail(userId, meetingId);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
    }

    @PatchMapping("")
    public ResponseEntity<BaseResponse<?>> updateMeetingInfo(@UserId final Long userId,
                                                             @RequestBody final UpdateMeetingInfoRequestDto requestDto) {
        final UpdateMeetingInfoResponseDto responseDto = meetingService.updateMeetingInfo(userId, requestDto);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
    }

}
