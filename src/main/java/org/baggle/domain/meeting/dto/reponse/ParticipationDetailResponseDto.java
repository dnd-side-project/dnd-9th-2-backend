package org.baggle.domain.meeting.dto.reponse;

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

    @Builder
    public ParticipationDetailResponseDto(Long memberId, String nickname, String profileImageUrl, MeetingAuthority meetingAuthority, ButtonAuthority buttonAuthority, Feed feed) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.meetingAuthority = (meetingAuthority == MeetingAuthority.HOST) ? Boolean.TRUE : Boolean.FALSE;
        this.buttonAuthority = (buttonAuthority == ButtonAuthority.OWNER) ? Boolean.TRUE : Boolean.FALSE;
        this.feedId = Objects.isNull(feed) ? null : feed.getId();
        this.feedImageUrl = Objects.isNull(feed) ? "" : feed.getFeedImageUrl();
    }

    public static ParticipationDetailResponseDto of(Participation participation, User user, Feed feed) {
        return ParticipationDetailResponseDto.builder()
                .memberId(participation.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .meetingAuthority(participation.getMeetingAuthority())
                .buttonAuthority(participation.getButtonAuthority())
                .feed(feed)
                .build();
    }
}
