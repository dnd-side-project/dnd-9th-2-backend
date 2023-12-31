package org.baggle.domain.meeting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.domain.user.domain.User;
import org.baggle.global.common.BaseTimeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.baggle.domain.meeting.domain.Participation.createParticipation;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Entity
public class Meeting extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Participation> participations = new ArrayList<>();
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String place;
    private LocalDate date;
    private LocalTime time;
    private String memo;
    @Enumerated(value = EnumType.STRING)
    private MeetingStatus meetingStatus;

    public static Meeting createMeeting(User user, String title, String place, LocalDate date, LocalTime time, String memo) {
        Meeting meeting = Meeting.builder()
                .title(title)
                .place(place)
                .date(date)
                .time(time)
                .memo(memo)
                .meetingStatus(MeetingStatus.SCHEDULED)
                .build();
        Participation participation = createParticipation();
        participation.changeUser(user);
        participation.changeMeeting(meeting);
        return meeting;
    }

    public void updateMeetingInfo(String title, String place, LocalDateTime dateTime, String memo) {
        this.title = (title != null) ? title : this.title;
        this.place = (place != null) ? place : this.place;
        this.date = (dateTime != null) ? dateTime.toLocalDate() : this.date;
        this.time = (dateTime != null) ? dateTime.toLocalTime() : this.time;
        this.memo = (memo != null) ? memo : this.memo;
    }

    public void updateMeetingStatusInto(MeetingStatus meetingStatus) {
        this.meetingStatus = meetingStatus;
    }

    public void initButtonAuthorityOfParticipationList() {
        this.participations.forEach(participation -> participation.updateButtonAuthority(ButtonAuthority.NON_OWNER));
    }

    public void addParticipation(Participation newParticipation) {
        this.participations.add(newParticipation);
    }

    public Participation getRandomNumberParticipation(int randomNumber) {
        return getParticipations().get(randomNumber);
    }

    public void withdrawParticipation(Participation participation) {
        this.participations.remove(participation);
    }
}
