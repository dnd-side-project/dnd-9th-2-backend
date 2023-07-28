package org.baggle.domain.sample.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.sample.entity.Sample;


@Getter
public class SampleResponseDto {
    private Long id;
    @Builder
    public SampleResponseDto(Long id) {
        this.id = id;
    }

    public static SampleResponseDto of(Sample sample) {
        return SampleResponseDto.builder()
                .id(sample.getId())
                .build();
    }
}