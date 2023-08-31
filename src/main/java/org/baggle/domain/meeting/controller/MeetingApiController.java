package org.baggle.domain.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.meeting.dto.request.CreateMeetingRequestDto;
import org.baggle.domain.meeting.dto.request.UpdateMeetingInfoRequestDto;
import org.baggle.domain.meeting.dto.response.CreateMeetingResponseDto;
import org.baggle.domain.meeting.dto.response.GetMeetingResponseDto;
import org.baggle.domain.meeting.dto.response.MeetingDetailResponseDto;
import org.baggle.domain.meeting.dto.response.UpdateMeetingInfoResponseDto;
import org.baggle.domain.meeting.service.MeetingDetailService;
import org.baggle.domain.meeting.service.MeetingService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.baggle.global.config.auth.UserId;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/meeting")
@Controller
public class MeetingApiController {
    private final MeetingService meetingService;
    private final MeetingDetailService meetingDetailService;

    @PostMapping
    public ResponseEntity<BaseResponse<?>> createMeeting(@UserId final Long userId,
                                                         @RequestBody final CreateMeetingRequestDto createMeetingRequestDto) {
        final CreateMeetingResponseDto createMeetingResponseDto = meetingService.createMeeting(userId, createMeetingRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.of(SuccessCode.CREATED, createMeetingResponseDto));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<?>> getMeetings(@UserId final Long userId,
                                                       @RequestParam final String period,
                                                       final Pageable pageable) {
        final GetMeetingResponseDto getMeetingResponseDto = meetingService.getMeetings(userId, period, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, getMeetingResponseDto));
    }

    @GetMapping("/detail")
    public ResponseEntity<BaseResponse<?>> findMeetingDetail(@UserId final Long userId,
                                                             @RequestParam final Long meetingId) {
        final MeetingDetailResponseDto responseDto = meetingDetailService.findMeetingDetail(userId, meetingId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, responseDto));
    }

    @PatchMapping
    public ResponseEntity<BaseResponse<?>> updateMeetingInfo(@UserId final Long userId,
                                                             @RequestBody final UpdateMeetingInfoRequestDto requestDto) {
        final UpdateMeetingInfoResponseDto responseDto = meetingDetailService.updateMeetingInfo(userId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, responseDto));
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse<?>> deleteMeeting(@UserId final Long userId,
                                                         @RequestParam final Long meetingId){
        meetingDetailService.deleteMeetingInfo(userId, meetingId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.of(SuccessCode.OK, null));
    }
}
