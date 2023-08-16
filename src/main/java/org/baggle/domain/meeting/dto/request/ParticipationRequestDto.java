package org.baggle.domain.meeting.dto.request;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ParticipationRequestDto {
    private Long meetingId;
}
