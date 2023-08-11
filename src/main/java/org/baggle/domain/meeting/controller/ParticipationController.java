package org.baggle.domain.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.dto.reponse.ParticipationAvailabilityResponseDto;
import org.baggle.domain.meeting.dto.reponse.ParticipationResponseDto;
import org.baggle.domain.meeting.dto.request.ParticipationReqeustDto;
import org.baggle.domain.meeting.service.ParticipationService;
import org.baggle.domain.user.service.AuthService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/api/member")
@Controller
public class ParticipationController {
    private final AuthService authService;
    private final ParticipationService participationService;

//    @GetMapping()
//    public ResponseEntity<BaseResponse<?>> findMeetingAvailability(@RequestParam final Long meetingId) {
//        final ParticipationAvailabilityResponseDto responseDto = participationService.findParticipationAvailability(, meetingId);
//        if (Objects.isNull(responseDto))
//            return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, null));
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
//    }
//
//    @PostMapping()
//    public ResponseEntity<BaseResponse<?>> createParticipation(@RequestBody final ParticipationReqeustDto requestDto) {
//        System.out.println(requestDto);
//        final ParticipationResponseDto responseDto = participationService.createParticipation(, requestDto);
//        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
//    }
}
