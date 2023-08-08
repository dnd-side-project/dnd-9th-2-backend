package org.baggle.domain.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.dto.reponse.MeetingDetailResponseDto;
import org.baggle.domain.meeting.service.MeetingService;
import org.baggle.domain.meeting.service.ParticipationService;
import org.baggle.domain.user.service.AuthService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/meeting")
@Controller
public class MeetingController {
    private final AuthService authService;
    private final MeetingService meetingService;

//    public ResponseEntity<BaseResponse<?>> findMeetingDetail(@RequestHeader("Authorization") final ________,
//                                                             @PathVariable("meeting_id") final Long requestId){
//
//        MeetingDetailResponseDto responseDto = meetingService.findMeetingDetail(requestId);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
//    }

}
