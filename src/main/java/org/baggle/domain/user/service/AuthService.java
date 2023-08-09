package org.baggle.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.user.auth.apple.AppleOAuthProvider;
import org.baggle.domain.user.domain.Platform;
import org.baggle.domain.user.domain.RefreshToken;
import org.baggle.domain.user.domain.User;
import org.baggle.domain.user.dto.request.UserSignInRequestDto;
import org.baggle.domain.user.dto.response.UserAuthResponseDto;
import org.baggle.domain.user.repository.RefreshTokenRepository;
import org.baggle.domain.user.repository.UserRepository;
import org.baggle.global.common.ImageType;
import org.baggle.global.config.jwt.JwtProvider;
import org.baggle.global.config.jwt.Token;
import org.baggle.global.error.exception.ConflictException;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.infra.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Service s3Service;
    private final JwtProvider jwtProvider;
    private final AppleOAuthProvider appleOAuthProvider;

    // TODO
    public UserAuthResponseDto signin(String token, UserSignInRequestDto userSignInRequestDto) {
        return null;
    }

    public UserAuthResponseDto signup(String token, MultipartFile image, String nickname, String platform, String fcmToken) {
        Platform enumPlatform = getEnumPlatformFromStringPlatform(platform);
        String platformId = getPlatformIdFromToken(token, enumPlatform);
        validateDuplicateUser(enumPlatform, platformId);
        String profileImageUrl = uploadProfileImageToS3AndGetProfileImageUrl(image, ImageType.PROFILE);
        User user = User.createUserWithFcmToken(profileImageUrl, nickname, fcmToken, platformId, enumPlatform);
        User savedUser = userRepository.save(user);
        Token issuedToken = jwtProvider.issueToken(savedUser.getId());
        updateRefreshToken(savedUser, issuedToken.getRefreshToken());
        return UserAuthResponseDto.of(issuedToken, savedUser);
    }

    // TODO
    public Token reissue(String refreshToken) {
        return null;
    }

    private Platform getEnumPlatformFromStringPlatform(String platform) {
        return Platform.getEnumPlatformFromStringPlatform(platform);
    }

    private String getPlatformIdFromToken(String token, Platform platform) {
        if (platform == Platform.KAKAO) {
            // TODO
            return null;
        }
        return appleOAuthProvider.getApplePlatformId(token);
    }

    private void validateDuplicateUser(Platform platform, String platformId) {
        List<User> findUsers = userRepository.findUsersByPlatformAndPlatformId(platform, platformId);
        if (!findUsers.isEmpty()) {
            throw new ConflictException(ErrorCode.DUPLICATE_USER);
        }
    }

    private void updateRefreshToken(User user, String refreshToken) {
        user.updateRefreshToken(refreshToken);
        refreshTokenRepository.save(RefreshToken.createRefreshToken(user.getId(), refreshToken));
    }

    private String uploadProfileImageToS3AndGetProfileImageUrl(MultipartFile image, ImageType imageType) {
        return s3Service.uploadFile(image, imageType.getImageType());
    }
}
