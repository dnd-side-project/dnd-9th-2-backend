package org.baggle.domain.sample.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.baggle.domain.sample.domain.Sample;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SampleRequestDto {
    private String data;

    public Sample toEntity() {
        return Sample.builder()
                .data(data)
                .build();
    }
}