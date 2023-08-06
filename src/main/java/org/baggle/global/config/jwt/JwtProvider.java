package org.baggle.global.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.InvalidValueException;
import org.baggle.global.error.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Getter
@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-expire-time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${jwt.refresh-token-expire-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    public Token issueToken(Long userId) {
        return Token.of(generateToken(userId, true), generateToken(userId, false));
    }

    private String generateToken(Long userId, boolean isAccessToken) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + ((isAccessToken) ? ACCESS_TOKEN_EXPIRE_TIME : REFRESH_TOKEN_EXPIRE_TIME));
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateAccessToken(String accessToken) {
        try {
            getJwtParser().parseClaimsJwt(accessToken);
        } catch (JwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_ACCESS_TOKEN);
        } catch (IllegalArgumentException ee) {
            throw new InvalidValueException(ErrorCode.BAD_REQUEST);
        }
    }

    public void validateRefreshToken(String refreshToken) {
        try {
            getJwtParser().parseClaimsJwt(refreshToken);
        } catch (JwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN);
        } catch (IllegalArgumentException ee) {
            throw new InvalidValueException(ErrorCode.BAD_REQUEST);
        }
    }

    public Long getSubject(String token) {
        return Long.valueOf(getJwtParser().parseClaimsJwt(token)
                .getBody()
                .getSubject());
    }

    public JwtParser getJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
    }

    public Key getSigningKey() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }
}