package org.baggle.domain.report.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.InvalidValueException;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ReportType {
    SEXUALLY("sexually"),
    VIOLENT("violent"),
    UNPLEASANT("unpleasant");

    private final String stringReportType;

    public static ReportType getEnumReportTypeFromStringReportType(String stringReportType) {
        return Arrays.stream(values())
                .filter(reportType -> reportType.stringReportType.equals(stringReportType))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.INVALID_REPORT_TYPE));
    }
}
