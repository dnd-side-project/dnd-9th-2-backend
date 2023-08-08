package org.baggle.domain.fcm.repository;

import org.baggle.domain.fcm.domain.FcmTimer;
import org.springframework.data.repository.CrudRepository;

public interface FcmTimerRepository extends CrudRepository<FcmTimer, Long> {
}
