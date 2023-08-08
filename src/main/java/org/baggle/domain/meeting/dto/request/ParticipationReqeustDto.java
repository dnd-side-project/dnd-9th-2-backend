package org.baggle.domain.meeting.dto.request;


import lombok.Getter;
import org.baggle.domain.meeting.domain.*;
import org.baggle.domain.user.domain.User;

@Getter
public class ParticipationReqeustDto {
    private Long meetingId;

    public Participation toEntity(User user, Meeting meeting, MeetingAuthority meetingAuthority, ParticipationMeetingStatus participationMeetingStatus, ButtonAuthority buttonAuthority){
        return Participation.builder()
                .user(user)
                .meeting(meeting)
                .participationMeetingStatus(participationMeetingStatus)
                .buttonAuthority(buttonAuthority)
                .meetingAuthority(meetingAuthority)
                .build();
    }
}
