package org.baggle.domain.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.service.ParticipationService;
import org.baggle.domain.user.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
//            return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, true));
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
