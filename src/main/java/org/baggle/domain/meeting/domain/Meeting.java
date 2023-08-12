package org.baggle.domain.meeting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.global.common.BaseTimeEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Meeting extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;
    @OneToMany(mappedBy = "meeting")
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

    public void updateTitleAndPlaceAndDateAndTimeAndMemo(String title, String place, LocalDate date, LocalTime time, String memo) {
        this.title = (title != null) ? title : this.title;
        this.place = (place != null) ? place : this.place;
        this.date = (date != null) ? date : this.date;
        this.time = (time != null) ? time : this.time;
        this.memo = (memo != null) ? memo : this.place;

    }
}
