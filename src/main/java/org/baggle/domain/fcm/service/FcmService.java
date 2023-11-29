package org.baggle.domain.fcm.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.fcm.repository.FcmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class FcmService {
    private final FcmRepository fcmRepository;

    public List<FcmToken> findFcmTokens(Long meetingId) {
        return fcmRepository.findByUserParticipationsMeetingId(meetingId);
    }
}
