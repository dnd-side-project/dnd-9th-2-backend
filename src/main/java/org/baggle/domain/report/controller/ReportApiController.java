package org.baggle.domain.report.controller;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.report.dto.request.CreateReportRequestDto;
import org.baggle.domain.report.service.ReportService;
import org.baggle.global.common.BaseResponse;
import org.baggle.global.common.SuccessCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/api/report")
@Controller
public class ReportApiController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<BaseResponse<?>> createReport(@RequestBody CreateReportRequestDto requestDto) {
        reportService.createReport(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.of(SuccessCode.CREATED, null));
    }
}
