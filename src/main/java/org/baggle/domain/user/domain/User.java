package org.baggle.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.domain.fcm.domain.FCMToken;
import org.baggle.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String profileImageUrl;
    @Column(nullable = false)
    private String nickname;
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private FCMToken fcmToken;
}
