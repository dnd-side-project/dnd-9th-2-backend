package org.baggle.domain.meeting.dto.reponse;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ParticipationResponseDto {
    private Long participationId;

    @Builder
    public ParticipationResponseDto(Long participationId) {
        this.participationId = participationId;
    }

    public static ParticipationResponseDto of(Long participationId){
        return ParticipationResponseDto.builder()
                .participationId(participationId)
                .build();
    }
}
