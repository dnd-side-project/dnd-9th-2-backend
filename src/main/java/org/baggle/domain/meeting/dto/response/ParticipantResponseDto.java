package org.baggle.domain.meeting.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.baggle.domain.user.domain.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ParticipantResponseDto {
    private String profileImageUrl;

    public static ParticipantResponseDto of(User user) {
        return new ParticipantResponseDto(user.getProfileImageUrl());
    }
}
