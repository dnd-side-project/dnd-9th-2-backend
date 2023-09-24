package org.baggle.global.config.jwt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Token {
    private String accessToken;
    private String refreshToken;

    public static Token of(String accessToken, String refreshToken) {
        return new Token(accessToken, refreshToken);
    }
}
