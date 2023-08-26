package org.baggle.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.repository.FcmRepository;
import org.baggle.domain.user.auth.apple.AppleOAuthProvider;
import org.baggle.domain.user.auth.kakao.KakaoOAuthProvider;
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
import org.baggle.infra.s3.S3Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.baggle.domain.user.domain.Platform.getEnumPlatformFromStringPlatform;
import static org.baggle.domain.user.domain.RefreshToken.createRefreshToken;
import static org.baggle.domain.user.domain.User.createUser;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FcmRepository fcmRepository;
    private final S3Provider s3Provider;
    private final JwtProvider jwtProvider;
    private final AppleOAuthProvider appleOAuthProvider;
    private final KakaoOAuthProvider kakaoOAuthProvider;

    public UserAuthResponseDto signIn(String token, UserSignInRequestDto userSignInRequestDto) {
        Platform enumPlatform = getEnumPlatformFromStringPlatform(userSignInRequestDto.getPlatform());
        String platformId = getPlatformId(token, enumPlatform);
        User findUser = getUser(enumPlatform, platformId);
        updateFcmToken(userSignInRequestDto.getFcmToken(), findUser);
        Token issuedToken = issueAccessTokenAndRefreshToken(findUser);
        return UserAuthResponseDto.of(issuedToken, findUser);
    }

    public UserAuthResponseDto signUp(String token, MultipartFile image, String nickname, String platform, String fcmToken) {
        validateDuplicateNickname(nickname);
        Platform enumPlatform = getEnumPlatformFromStringPlatform(platform);
        String platformId = getPlatformId(token, enumPlatform);
        validateDuplicateUser(enumPlatform, platformId);
        User savedUser = saveUser(image, nickname, fcmToken, enumPlatform, platformId);
        Token issuedToken = issueAccessTokenAndRefreshToken(savedUser);
        updateRefreshToken(issuedToken.getRefreshToken(), savedUser);
        return UserAuthResponseDto.of(issuedToken, savedUser);
    }

    public Token reissue(String refreshToken) {
        jwtProvider.validateRefreshToken(refreshToken);
        Long userId = jwtProvider.getSubject(refreshToken);
        String storedRefreshToken = getRefreshToken(userId);
        jwtProvider.equalsRefreshToken(refreshToken, storedRefreshToken);
        User findUser = getUser(userId);
        Token issuedToken = issueAccessTokenAndRefreshToken(findUser);
        updateRefreshToken(issuedToken.getRefreshToken(), findUser);
        return issuedToken;
    }

    public void signOut(Long userId) {
        User findUser = getUser(userId);
        deleteRefreshToken(findUser);
        deleteFcmToken(findUser);
    }

    public void withdraw(Long userId) {
        User findUser = getUser(userId);
        findUser.withdrawUser();
        deleteRefreshToken(findUser);
        deleteFcmToken(findUser);
    }

    private User getUser(Platform platform, String platformId) {
        return userRepository.findUserByPlatformAndPlatformId(platform, platformId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private void updateFcmToken(String fcmToken, User user) {
        FcmToken findFcmToken = getFcmToken(user);
        findFcmToken.updateFcmToken(fcmToken);
    }

    private void validateDuplicateNickname(String nickname) {
        if (userRepository.existsUserByNickname(nickname)) {
            throw new ConflictException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validateDuplicateUser(Platform platform, String platformId) {
        if (userRepository.existsUserByPlatformAndPlatformId(platform, platformId)) {
            throw new ConflictException(ErrorCode.DUPLICATE_USER);
        }
    }

    private User saveUser(MultipartFile image, String nickname, String fcmToken, Platform platform, String platformId) {
        String profileImageUrl = hasMultipartFile(image) ? uploadImageToS3AndGetImageUrl(image) : null;
        User user = createUser(profileImageUrl, nickname, fcmToken, platform, platformId);
        return userRepository.save(user);
    }

    private String getPlatformId(String token, Platform platform) {
        if (platform == Platform.KAKAO) {
            return kakaoOAuthProvider.getKakaoPlatformId(token);
        }
        return appleOAuthProvider.getApplePlatformId(token);
    }

    private String getRefreshToken(Long userId) {
        try {
            return getRefreshTokenFromRedis(userId);
        } catch (EntityNotFoundException e) {
            User findUser = getUser(userId);
            return findUser.getRefreshToken();
        }
    }

    private Token issueAccessTokenAndRefreshToken(User user) {
        return jwtProvider.issueToken(user.getId());
    }

    private void updateRefreshToken(String refreshToken, User user) {
        user.updateRefreshToken(refreshToken);
        refreshTokenRepository.save(createRefreshToken(user.getId(), refreshToken));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private void deleteRefreshToken(User user) {
        user.updateRefreshToken(null);
        refreshTokenRepository.deleteById(user.getId());
    }

    private void deleteFcmToken(User user) {
        FcmToken findfcmToken = user.getFcmToken();
        findfcmToken.updateFcmToken(null);
    }

    private FcmToken getFcmToken(User user) {
        return fcmRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FCM_TOKEN_NOT_FOUND));
    }

    private boolean hasMultipartFile(MultipartFile multipartFile) {
        return multipartFile != null && !multipartFile.isEmpty();
    }

    private String uploadImageToS3AndGetImageUrl(MultipartFile image) {
        return s3Provider.uploadFile(image, ImageType.PROFILE.getImageType());
    }

    private String getRefreshTokenFromRedis(Long userId) {
        RefreshToken storedRefreshToken = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        return storedRefreshToken.getRefreshToken();
    }
}