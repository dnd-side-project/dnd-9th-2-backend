package org.baggle.domain.user.auth.apple;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AppleOAuthProvider {
    private final AppleFeignClient appleFeignClient;
    private final IdentityTokenParser identityTokenParser;
    private final PublicKeyGenerator publicKeyGenerator;
    private final IdentityTokenValidator identityTokenValidator;

    public String getApplePlatformId(String identityToken) {
        Map<String, String> headers = identityTokenParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleFeignClient.getApplePublicKeys();
        PublicKey publicKey = publicKeyGenerator.generatePublicKeyWithApplePublicKeys(headers, applePublicKeys);
        Claims claims = identityTokenParser.parseWithPublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);
        return claims.getSubject();
    }

    private void validateClaims(Claims claims) {
        if (!identityTokenValidator.isValidIdentityToken(claims)) {
            throw new UnauthorizedException(ErrorCode.INVALID_IDENTITY_TOKEN);
        }
    }
}
