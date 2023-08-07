package org.baggle.domain.fcm.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.dto.request.AddFcmTokenRequestDto;
import org.baggle.domain.fcm.dto.request.UpdateFcmTokenRequestDto;
import org.baggle.domain.fcm.dto.response.AddFcmTokenResponseDto;
import org.baggle.domain.fcm.dto.response.GetFcmTokenResponseDto;
import org.baggle.domain.fcm.dto.response.UpdateFcmTokenResponseDto;
import org.baggle.domain.fcm.repository.FcmRepository;
import org.baggle.domain.user.domain.User;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.baggle.global.error.exception.ErrorCode.FCM_TOKEN_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class FcmService {
    private final FcmRepository fcmRepository;

    public GetFcmTokenResponseDto findFcmTokens(User user) {
        FcmToken fcmToken = fcmRepository.findByUser(user);

        return GetFcmTokenResponseDto.of(fcmToken);
    }

    public AddFcmTokenResponseDto createFcmToken(AddFcmTokenRequestDto requestDto, User user) {
        FcmToken fcmToken = requestDto.toEntity(user);
        fcmRepository.save(fcmToken);

        return AddFcmTokenResponseDto.of(fcmToken);
    }

    public UpdateFcmTokenResponseDto updateFcmToken(UpdateFcmTokenRequestDto requestDto) {
        FcmToken fcmToken = fcmRepository.findByFcmToken(requestDto.getBeforeFCMToken())
                .orElseThrow(() -> new EntityNotFoundException(FCM_TOKEN_NOT_FOUND));
        fcmToken.updateFcmToken(requestDto.getUpdateFCMToken());

        return UpdateFcmTokenResponseDto.of(fcmToken);
    }

}
