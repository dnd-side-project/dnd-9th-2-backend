package org.baggle.domain.user.repository;

import org.baggle.domain.user.domain.Platform;
import org.baggle.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUsersByPlatformAndPlatformId(Platform platform, String platformId);
}
