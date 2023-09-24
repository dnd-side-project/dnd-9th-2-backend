package org.baggle.domain.meeting.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.meeting.domain.ButtonAuthority;
import org.baggle.domain.meeting.domain.MeetingAuthority;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.domain.user.domain.User;

import java.util.Objects;

@Builder
@Getter
public class ParticipationDetailResponseDto {
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private boolean meetingAuthority;
    private boolean buttonAuthority;
    private Long feedId;
    private String feedImageUrl;
    private boolean report;

    public static ParticipationDetailResponseDto of(Participation participation, boolean report) {
        User participationUser = participation.getUser();
        Feed participationFeed = participation.getFeed();
        return ParticipationDetailResponseDto.builder()
                .memberId(participation.getId())
                .nickname(participationUser.getNickname())
                .profileImageUrl(participationUser.getProfileImageUrl())
                .meetingAuthority(participation.getMeetingAuthority() == MeetingAuthority.HOST)
                .buttonAuthority(participation.getButtonAuthority() == ButtonAuthority.OWNER)
                .feedId(Objects.isNull(participationFeed) ? null : participationFeed.getId())
                .feedImageUrl(Objects.isNull(participationFeed) ? "" : participationFeed.getFeedImageUrl())
                .report(report)
                .build();
    }
}
