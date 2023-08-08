package org.baggle.domain.user.auth.apple;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdentityTokenValidator {
    @Value("${oauth.apple.iss}")
    private String iss;
    @Value("${oauth.apple.client-id}")
    private String clientId;

    public boolean isValidIdentityToken(Claims claims) {
        return claims.getIssuer().contains(iss)
                && claims.getAudience().equals(clientId);
    }
}
