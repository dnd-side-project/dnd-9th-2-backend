package org.baggle.global.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface TimeConverter {
    static LocalDate convertToLocalDate(LocalDateTime meetingTime) {
        return meetingTime.toLocalDate();
    }

    static LocalTime convertToLocalTime(LocalDateTime meetingTime) {
        return meetingTime.toLocalTime();
    }
}
