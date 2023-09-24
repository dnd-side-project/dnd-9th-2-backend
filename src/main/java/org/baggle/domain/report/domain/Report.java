package org.baggle.domain.report.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Report extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id")
    private Participation participation;
    @Enumerated(value = EnumType.STRING)
    private ReportType reportType;

    public static Report createReport(Feed feed, Participation participation, ReportType reportType) {
        Report createdReport = Report.builder()
                .feed(feed)
                .participation(participation)
                .reportType(reportType)
                .build();
        feed.addReport(createdReport);
        participation.addReport(createdReport);
        return createdReport;
    }
}