package org.baggle.domain.feed.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.domain.report.domain.Report;
import org.baggle.global.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Feed extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id")
    private Participation participation;
    @Column(nullable = false)
    private String feedImageUrl;
    @OneToMany(mappedBy = "feed")
    @Builder.Default
    private List<Report> reports = new ArrayList<>();

    public static Feed createParticipationWithFeedImg(Participation participation, String feedImageUrl) {
        return Feed.builder()
                .participation(participation)
                .feedImageUrl(feedImageUrl)
                .build();
    }

    public void addReport(Report report) {
        this.reports.add(report);
    }
}
