package org.baggle.domain.meeting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.user.domain.User;
import org.baggle.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Participation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;
    @OneToOne(mappedBy = "participation", fetch = FetchType.LAZY)
    private Feed feed;
    @OneToOne(mappedBy = "participation", fetch = FetchType.LAZY)
    private Report report;
    @Enumerated(value = EnumType.STRING)
    private MeetingAuthority meetingAuthority;
    @Enumerated(value = EnumType.STRING)
    private ParticipationMeetingStatus participationMeetingStatus;
    @Enumerated(value = EnumType.STRING)
    private ButtonAuthority buttonAuthority;

    public static Participation createParticipation() {
        return Participation.builder()
                .meetingAuthority(MeetingAuthority.HOST)
                .participationMeetingStatus(ParticipationMeetingStatus.PARTICIPATING)
                .buttonAuthority(ButtonAuthority.NON_OWNER)
                .build();
    }

    public static Participation createParticipationWithoutFeed(User user, Meeting meeting, MeetingAuthority meetingAuthority, ParticipationMeetingStatus participationMeetingStatus, ButtonAuthority buttonAuthority) {
        return Participation.builder()
                .user(user)
                .meeting(meeting)
                .participationMeetingStatus(participationMeetingStatus)
                .buttonAuthority(buttonAuthority)
                .meetingAuthority(meetingAuthority)
                .build();
    }

    public void changeUser(User user) {
        this.user = user;
        user.getParticipations().add(this);
    }

    public void changeMeeting(Meeting meeting) {
        this.meeting = meeting;
        meeting.getParticipations().add(this);
    }
}
