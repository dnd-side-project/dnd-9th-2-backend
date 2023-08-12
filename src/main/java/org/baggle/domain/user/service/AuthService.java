package org.baggle.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.repository.FcmRepository;
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
import org.baggle.global.error.exception.EntityNotFoundException;
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
    private final FcmRepository fcmRepository;
    private final S3Service s3Service;
    private final JwtProvider jwtProvider;
    private final AppleOAuthProvider appleOAuthProvider;

    public UserAuthResponseDto signIn(String token, UserSignInRequestDto userSignInRequestDto) {
        User findUser = getUser(token, userSignInRequestDto);
        updateFcmToken(userSignInRequestDto.getFcmToken(), findUser);
        Token issuedToken = issueAccessTokenAndRefreshToken(findUser);
        return UserAuthResponseDto.of(issuedToken, findUser);
    }

    public UserAuthResponseDto signUp(String token, MultipartFile image, String nickname, String platform, String fcmToken) {
        User savedUser = validateAndSaveUser(token, image, nickname, platform, fcmToken);
        Token issuedToken = issueAccessTokenAndRefreshToken(savedUser);
        updateRefreshToken(savedUser, issuedToken.getRefreshToken());
        return UserAuthResponseDto.of(issuedToken, savedUser);
    }

    public void withdraw(Long userId) {
        User findUser = getUser(userId);
        findUser.withdrawUser();
        deleteRefreshToken(userId);
    }

    // TODO
    public Token reissue(String refreshToken) {
        return null;
    }

    private User getUser(String token, UserSignInRequestDto userSignInRequestDto) {
        Platform enumPlatform = getEnumPlatformFromStringPlatform(userSignInRequestDto.getPlatform());
        String platformId = getPlatformIdFromToken(token, enumPlatform);
        return getUserByPlatformAndPlatformId(enumPlatform, platformId);
    }

    private User getUser(Long userId) {
        return getUserById(userId);
    }

    private void deleteRefreshToken(Long userId) {
        refreshTokenRepository.deleteById(userId);
    }

    private void updateFcmToken(String fcmToken, User user) {
        FcmToken findFcmToken = getFcmTokenByUserId(user.getId());
        findFcmToken.updateFcmToken(fcmToken);
    }

    private User validateAndSaveUser(String token, MultipartFile image, String nickname, String platform, String fcmToken) {
        validateDuplicateNickname(nickname);
        Platform enumPlatform = getEnumPlatformFromStringPlatform(platform);
        String platformId = getPlatformIdFromToken(token, enumPlatform);
        validateDuplicateUser(enumPlatform, platformId);
        String profileImageUrl = hasMultipartFile(image) ? uploadProfileImageToS3AndGetProfileImageUrl(image, ImageType.PROFILE) : "";
        User user = User.createUserWithFcmToken(profileImageUrl, nickname, fcmToken, platformId, enumPlatform);
        return userRepository.save(user);
    }

    private Token issueAccessTokenAndRefreshToken(User user) {
        return jwtProvider.issueToken(user.getId());
    }

    private void updateRefreshToken(User user, String refreshToken) {
        user.updateRefreshToken(refreshToken);
        refreshTokenRepository.save(RefreshToken.createRefreshToken(user.getId(), refreshToken));
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

    private User getUserByPlatformAndPlatformId(Platform platform, String platformId) {
        return userRepository.findUserByPlatformAndPlatformId(platform, platformId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private FcmToken getFcmTokenByUserId(Long userId) {
        return fcmRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FCM_TOKEN_NOT_FOUND));
    }

    private void validateDuplicateNickname(String nickname) {
        List<User> findUsers = userRepository.findUsersByNickname(nickname);
        if (!findUsers.isEmpty()) {
            throw new ConflictException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validateDuplicateUser(Platform platform, String platformId) {
        List<User> findUsers = userRepository.findUsersByPlatformAndPlatformId(platform, platformId);
        if (!findUsers.isEmpty()) {
            throw new ConflictException(ErrorCode.DUPLICATE_USER);
        }
    }

    private boolean hasMultipartFile(MultipartFile multipartFile) {
        return multipartFile != null && !multipartFile.isEmpty();
    }

    private String uploadProfileImageToS3AndGetProfileImageUrl(MultipartFile image, ImageType imageType) {
        return s3Service.uploadFile(image, imageType.getImageType());
    }
}
