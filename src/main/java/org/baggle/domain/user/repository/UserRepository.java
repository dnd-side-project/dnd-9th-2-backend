package org.baggle.domain.user.repository;

import org.baggle.domain.user.domain.Platform;
import org.baggle.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByNickname(String nickname);

    boolean existsUserByPlatformAndPlatformId(Platform platform, String platformId);

    Optional<User> findUserByPlatformAndPlatformId(Platform platform, String platformId);
}
