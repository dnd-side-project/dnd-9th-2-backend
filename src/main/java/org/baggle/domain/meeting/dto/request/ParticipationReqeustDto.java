package org.baggle.domain.meeting.dto.request;


import lombok.Getter;
import org.baggle.domain.meeting.domain.*;
import org.baggle.domain.user.domain.User;

@Getter
public class ParticipationReqeustDto {
    private Long meetingId;
}
