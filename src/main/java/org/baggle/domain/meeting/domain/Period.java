package org.baggle.domain.meeting.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.InvalidValueException;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Period {
    SCHEDULED("scheduled"),
    PAST("past");

    private final String stringPeriod;

    public static Period getEnumPeriodFromStringPeriod(String stringPeriod) {
        return Arrays.stream(values())
                .filter(period -> period.stringPeriod.equals(stringPeriod))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.INVALID_PERIOD_TYPE));
    }
}
