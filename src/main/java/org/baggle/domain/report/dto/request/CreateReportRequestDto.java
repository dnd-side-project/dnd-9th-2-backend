package org.baggle.domain.report.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CreateReportRequestDto {
    private Long participationId;
    private Long feedId;
    private String reportType;
}
