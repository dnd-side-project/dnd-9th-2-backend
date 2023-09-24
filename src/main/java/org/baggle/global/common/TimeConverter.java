package org.baggle.global.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface TimeConverter {
    static LocalDate convertToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    static LocalTime convertToLocalTime(LocalDateTime localDateTime) {
        return localDateTime.toLocalTime();
    }

    static LocalDateTime convertToLocalDateTime(LocalDate localDate, LocalTime localTime) {
        return LocalDateTime.of(localDate, localTime);
    }

    static LocalDateTime convertToStartLocalDateTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }
}
