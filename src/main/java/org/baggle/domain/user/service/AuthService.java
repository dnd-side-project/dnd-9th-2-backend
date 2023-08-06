package org.baggle.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.user.dto.response.UserAuthResponseDto;
import org.baggle.domain.user.repository.UserRepository;
import org.baggle.global.config.jwt.Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final UserRepository userRepository;

    // TODO
    public UserAuthResponseDto signin(String token) {
        return new UserAuthResponseDto();
    }

    // TODO
    public UserAuthResponseDto signup(String token, MultipartFile image, String nickname, String platform) {
        return new UserAuthResponseDto();
    }

    // TODO
    public Token reissue(String refreshToken) {
        return null;
    }
}
