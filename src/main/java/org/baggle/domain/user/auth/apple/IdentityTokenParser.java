package org.baggle.domain.user.auth.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

@Component
public class IdentityTokenParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> parseHeaders(String identityToken) {
        try {
            String encoded = identityToken.split("\\.")[0];
            String decoded = new String(Base64.getUrlDecoder().decode(encoded));
            return objectMapper.readValue(decoded, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_IDENTITY_TOKEN);
        }
    }

    public Claims parseWithPublicKeyAndGetClaims(String identityToken, PublicKey publicKey) {
        try {
            return getJwtParser(publicKey)
                    .parseClaimsJws(identityToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_IDENTITY_TOKEN);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_IDENTITY_TOKEN_VALUE);
        }
    }

    private JwtParser getJwtParser(Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }
}
