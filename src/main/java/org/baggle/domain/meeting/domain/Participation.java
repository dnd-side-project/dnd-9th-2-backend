package org.baggle.domain.meeting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.report.domain.Report;
import org.baggle.domain.user.domain.User;
import org.baggle.global.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(mappedBy = "participation", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Report> reports = new ArrayList<>();
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
                .buttonAuthority(ButtonAuthority.OWNER)
                .build();
    }

    public static Participation createParticipationWithMeeting(User user, Meeting meeting) {
        Participation participation = Participation.builder()
                .user(user)
                .meeting(meeting)
                .participationMeetingStatus(ParticipationMeetingStatus.PARTICIPATING)
                .buttonAuthority(ButtonAuthority.NON_OWNER)
                .meetingAuthority(MeetingAuthority.PARTICIPATION)
                .build();
        meeting.addParticipation(participation);
        return participation;
    }

    public void changeUser(User user) {
        this.user = user;
        user.getParticipations().add(this);
    }

    public void changeMeeting(Meeting meeting) {
        this.meeting = meeting;
        meeting.getParticipations().add(this);
    }

    public void updateButtonAuthority(ButtonAuthority buttonAuthority) {
        this.buttonAuthority = buttonAuthority;
    }

    public void updateMeetingAuthorityToHost() {
        this.meetingAuthority = MeetingAuthority.HOST;
    }

    public void updateMeetingAuthorityToParticipation() {
        this.meetingAuthority = MeetingAuthority.PARTICIPATION;
    }
}
