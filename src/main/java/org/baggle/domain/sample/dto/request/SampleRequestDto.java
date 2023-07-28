package org.baggle.domain.sample.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.baggle.domain.sample.entity.Sample;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SampleRequestDto {
    private String data;

    @Builder
    public SampleRequestDto(String data) {
        this.data = data;
    }

    public Sample toEntity() {
        return Sample.builder()
                .data(data)
                .build();
    }
}