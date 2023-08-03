package org.baggle.domain.meeting.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.global.common.BaseTimeEntity;

@Getter
@Entity
public class Report extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id")
    private Participation participation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;
}
