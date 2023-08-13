package org.baggle.domain.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.dto.response.ParticipationAvailabilityResponseDto;
import org.baggle.domain.meeting.dto.response.ParticipationResponseDto;
import org.baggle.domain.meeting.dto.request.ParticipationReqeustDto;
import org.baggle.domain.meeting.service.ParticipationService;
import org.baggle.domain.user.service.AuthService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.baggle.global.config.auth.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/api/member")
@Controller
public class ParticipationController {
    private final ParticipationService participationService;

    @GetMapping
    public ResponseEntity<BaseResponse<?>> findMeetingAvailability(@UserId final Long userId,
                                                                   @RequestParam final Long meetingId) {
        final ParticipationAvailabilityResponseDto responseDto = participationService.findParticipationAvailability(userId, meetingId);
        if (Objects.isNull(responseDto))
            return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATED, true));
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<?>> createParticipation(@UserId final Long userId,
                                                               @RequestBody final ParticipationReqeustDto requestDto) {
        final ParticipationResponseDto responseDto = participationService.createParticipation(userId, requestDto);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, responseDto));
    }
}
