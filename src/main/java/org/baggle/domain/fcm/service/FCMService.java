package org.baggle.domain.fcm.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FCMToken;
import org.baggle.domain.fcm.dto.request.AddFCMTokenRequestDto;
import org.baggle.domain.fcm.dto.request.UpdateFCMTokenRequestDto;
import org.baggle.domain.fcm.dto.response.AddFCMTokenResponseDto;
import org.baggle.domain.fcm.dto.response.GetFCMTokenResponseDto;
import org.baggle.domain.fcm.dto.response.UpdateFCMTokenResponseDto;
import org.baggle.domain.fcm.repository.FCMRepository;
import org.baggle.domain.user.domain.User;
import org.baggle.global.error.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.baggle.global.error.exception.ErrorCode.FCM_TOKEN_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class FCMService {
    private final FCMRepository fcmRepository;

    public GetFCMTokenResponseDto getFcmTokens(User user){
        FCMToken fcmToken = fcmRepository.findByUser(user);
        String result = fcmToken.getFcmToken();

        return new GetFCMTokenResponseDto(result);
    }

    public AddFCMTokenResponseDto addFcmToken(AddFCMTokenRequestDto requestDto, User user){
        FCMToken newFcmToken = new FCMToken(requestDto.getFcmToken(), user);
        fcmRepository.save(newFcmToken);

        return new AddFCMTokenResponseDto(newFcmToken.getFcmToken());
    }

    public UpdateFCMTokenResponseDto updateFcmToken(UpdateFCMTokenRequestDto requestDto){
        FCMToken fcmToken = fcmRepository.findByFcmToken(requestDto.getBeforeFCMToken())
                .orElseThrow(() -> new BusinessException(FCM_TOKEN_NOT_FOUND));
        fcmToken.update(requestDto.getUpdateFCMToken());

        //save 안해줘도 ㄱㅊ나???
        return new UpdateFCMTokenResponseDto(requestDto.getUpdateFCMToken());
    }

}
