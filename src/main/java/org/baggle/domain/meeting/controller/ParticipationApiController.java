package org.baggle.domain.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.dto.request.ParticipationRequestDto;
import org.baggle.domain.meeting.dto.response.ParticipationAvailabilityResponseDto;
import org.baggle.domain.meeting.service.ParticipationService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.baggle.global.config.auth.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/member")
@Controller
public class ParticipationApiController {
    private final ParticipationService participationService;

    @GetMapping
    public ResponseEntity<BaseResponse<?>> findMeetingAvailability(@UserId final Long userId,
                                                                   @RequestParam final Long meetingId) {
        final ParticipationAvailabilityResponseDto responseDto = participationService.findParticipationAvailability(userId, meetingId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, responseDto));
    }

    @PostMapping("/participation")
    public ResponseEntity<BaseResponse<?>> createParticipation(@UserId final Long userId,
                                                               @RequestBody final ParticipationRequestDto requestDto) {
        participationService.createParticipation(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.of(SuccessCode.CREATED, null));
    }

    @PatchMapping("/delegate")
    public ResponseEntity<BaseResponse<?>> delegateMeetingHost(@RequestParam final Long fromMemberId,
                                                               @RequestParam final Long toMemberId) {
        participationService.delegateMeetingHost(fromMemberId, toMemberId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, null));
    }

    @PatchMapping("/withdraw")
    public ResponseEntity<BaseResponse<?>> withdrawMember(@RequestParam final Long memberId){
        participationService.withdrawMember(memberId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, null));
    }
}
