package org.baggle.domain.sample.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.sample.dto.request.SampleRequestDto;
import org.baggle.domain.sample.dto.response.SampleResponseDto;
import org.baggle.domain.sample.entity.Sample;
import org.baggle.domain.sample.exception.SampleDuplicateException;
import org.baggle.domain.sample.repository.SampleRepository;
import org.baggle.global.error.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class SampleService {
    private final SampleRepository sampleRepository;

    public SampleResponseDto createSample(SampleRequestDto sampleRequestDto) {
        Sample sample = sampleRequestDto.toEntity();
        validateDuplicateSample(sample.getData());
        Sample savedSample = sampleRepository.save(sample);
        return SampleResponseDto.of(savedSample);
    }

    private void validateDuplicateSample(String data) {
        List<Sample> findSamples = sampleRepository.findByData(data);
        if (!findSamples.isEmpty()) {
            throw new SampleDuplicateException(ErrorCode.CONFLICT);
        }
    }
}