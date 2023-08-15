package org.baggle.domain.feed.repository;

import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.meeting.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    Optional<Feed> findByParticipationId(Long participationId);
}
