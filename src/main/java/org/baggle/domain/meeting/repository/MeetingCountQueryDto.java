package org.baggle.domain.meeting.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MeetingCountQueryDto {
    private Long scheduledCount;
    private Long pastCount;
}
