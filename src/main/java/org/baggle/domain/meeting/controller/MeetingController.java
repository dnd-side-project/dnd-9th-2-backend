package org.baggle.domain.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.dto.reponse.MeetingDetailResponseDto;
import org.baggle.domain.meeting.service.MeetingService;
import org.baggle.domain.user.service.AuthService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/api/meeting")
@Controller
public class MeetingController {
    private final AuthService authService;
    private final MeetingService meetingService;

//    @GetMapping("/detail")
//    public ResponseEntity<BaseResponse<?>> findMeetingDetail(@RequestParam final Long meetingId){
//
//        MeetingDetailResponseDto responseDto = meetingService.findMeetingDetail(meetingId);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
//    }

}
