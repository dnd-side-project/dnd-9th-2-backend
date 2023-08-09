package org.baggle.domain.fcm.repository;

import org.baggle.domain.fcm.domain.FcmNotification;
import org.springframework.data.repository.CrudRepository;

public interface FcmNotificationRepository extends CrudRepository<FcmNotification, Long> {
}
