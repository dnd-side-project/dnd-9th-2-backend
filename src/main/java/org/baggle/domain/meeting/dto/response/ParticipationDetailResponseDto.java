package org.baggle.domain.meeting.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.meeting.domain.ButtonAuthority;
import org.baggle.domain.meeting.domain.MeetingAuthority;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.domain.user.domain.User;

import java.util.Objects;

@Getter
public class ParticipationDetailResponseDto {
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private Boolean meetingAuthority;
    private Boolean buttonAuthority;
    private Long feedId;
    private String feedImageUrl;
    private boolean report;

    @Builder
    public ParticipationDetailResponseDto(User user, Participation participation, Feed feed, boolean report) {
        this.memberId = participation.getId();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.meetingAuthority = (participation.getMeetingAuthority() == MeetingAuthority.HOST) ? Boolean.TRUE : Boolean.FALSE;
        this.buttonAuthority = (participation.getButtonAuthority() == ButtonAuthority.OWNER) ? Boolean.TRUE : Boolean.FALSE;
        this.feedId = Objects.isNull(feed) ? null : feed.getId();
        this.feedImageUrl = Objects.isNull(feed) ? "" : feed.getFeedImageUrl();
        this.report = report;
    }

    public static ParticipationDetailResponseDto of(Participation participation, boolean report) {
        return ParticipationDetailResponseDto.builder()
                .user(participation.getUser())
                .participation(participation)
                .feed(participation.getFeed())
                .report(report)
                .build();
    }
}
