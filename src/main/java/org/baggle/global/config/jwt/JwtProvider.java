package org.baggle.global.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
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
    private Key signatureKey;
    private JwtParser jwtParser;

    @PostConstruct
    protected void init() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        this.signatureKey = Keys.hmacShaKeyFor(secretKeyBytes);
        this.jwtParser = Jwts.parserBuilder().setSigningKey(getSignatureKey()).build();
    }

    public Token issueToken(Long userId) {
        return Token.of(createAccessToken(userId), createRefreshToken(userId));
    }

    private String createAccessToken(Long userId) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String createRefreshToken(Long userId) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);
        final String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
        // TODO refresh token -> redis 저장 로직 추가 예정입니다.
        return refreshToken;
    }

    public void validateAccessToken(String accessToken) {
        try {
            jwtParser.parseClaimsJwt(accessToken);
        } catch (JwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_ACCESS_TOKEN);
        } catch (IllegalArgumentException ee) {
            throw new InvalidValueException(ErrorCode.BAD_REQUEST);
        }
    }

    public Long getSubject(String accessToken) {
        return Long.valueOf(jwtParser.parseClaimsJwt(accessToken)
                .getBody()
                .getSubject());
    }
}