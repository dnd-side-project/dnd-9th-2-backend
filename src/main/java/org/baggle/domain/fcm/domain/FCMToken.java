package org.baggle.domain.fcm.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.domain.user.domain.User;
import org.baggle.global.common.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class FCMToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_id")
    private Long id;

    @Column(nullable = false)
    private String fcmToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public FCMToken(String fcmToken, User user) {
        this.fcmToken = fcmToken;
        this.user = user;
    }

    public void updateFcmToken(String fcmToken){
        this.fcmToken = fcmToken;
    }
}