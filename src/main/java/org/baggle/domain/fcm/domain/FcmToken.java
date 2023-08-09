package org.baggle.domain.fcm.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.domain.user.domain.User;
import org.baggle.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "fcm")
@Entity
public class FcmToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_id")
    private Long id;
    @Column(nullable = false)
    private String fcmToken;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void changeUser(User user) {
        this.user = user;
        user.changeFcmToken(this);
    }
}
