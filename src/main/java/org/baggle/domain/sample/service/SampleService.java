package org.baggle.domain.sample.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.sample.domain.Sample;
import org.baggle.domain.sample.dto.request.SampleRequestDto;
import org.baggle.domain.sample.exception.SampleDuplicateException;
import org.baggle.domain.sample.repository.SampleRepository;
import org.baggle.global.config.jwt.JwtProvider;
import org.baggle.global.config.jwt.Token;
import org.baggle.global.error.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SampleService {
    private final SampleRepository sampleRepository;
    private final JwtProvider jwtProvider;

    public Token createSample(String token, SampleRequestDto sampleRequestDto) {
        log.info("token: {}", token);
        Sample sample = sampleRequestDto.toEntity();
        validateDuplicateSample(sample.getData());
        Sample savedSample = sampleRepository.save(sample);
        return jwtProvider.issueToken(savedSample.getId());
    }

    private void validateDuplicateSample(String data) {
        List<Sample> findSamples = sampleRepository.findByData(data);
        if (!findSamples.isEmpty()) {
            throw new SampleDuplicateException(ErrorCode.CONFLICT);
        }
    }
}