package org.baggle.global.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
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
        final Date expiration = new Date(now.getTime() + (isAccessToken ? ACCESS_TOKEN_EXPIRE_TIME : REFRESH_TOKEN_EXPIRE_TIME));
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
            getJwtParser().parseClaimsJws(accessToken);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_ACCESS_TOKEN_VALUE);
        }
    }

    public void validateRefreshToken(String refreshToken) {
        try {
            getJwtParser().parseClaimsJws(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN_VALUE);
        }
    }

    public Long getSubject(String token) {
        return Long.valueOf(getJwtParser().parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    public JwtParser getJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
    }

    public Key getSigningKey() {
        String encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        return Keys.hmacShaKeyFor(encoded.getBytes());
    }
}