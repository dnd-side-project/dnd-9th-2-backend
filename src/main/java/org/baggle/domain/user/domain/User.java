package org.baggle.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.global.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

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
    private String nickname;
    @OneToMany(mappedBy = "user")
    private List<Participation> participations = new ArrayList<>();
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private FcmToken fcmToken;
    private String platformId;
    private String refreshToken;
    private Platform platform;
}
