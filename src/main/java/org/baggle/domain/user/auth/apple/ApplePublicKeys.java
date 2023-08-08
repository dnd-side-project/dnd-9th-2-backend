package org.baggle.domain.user.auth.apple;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.UnauthorizedException;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApplePublicKeys {
    private List<ApplePublicKey> keys;

    public ApplePublicKey getMatchesApplePublicKey(String alg, String kid) {
        return keys.stream()
                .filter(applePublicKey -> applePublicKey.getAlg().equals(alg) && applePublicKey.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_IDENTITY_TOKEN));
    }
}
