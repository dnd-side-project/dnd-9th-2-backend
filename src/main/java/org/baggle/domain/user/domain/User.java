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
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private FcmToken fcmToken;
    private String platformId;
    private String refreshToken;
    @Enumerated(value = EnumType.STRING)
    private Platform platform;

    public static User createUserWithFcmToken(String profileImageUrl, String nickname, String fcmToken, String platformId, Platform platform) {
        User user = User.builder()
                .profileImageUrl(!profileImageUrl.isEmpty() ? profileImageUrl : null)
                .nickname(nickname)
                .platformId(platformId)
                .platform(platform)
                .build();
        FcmToken token = FcmToken.builder()
                .fcmToken(fcmToken)
                .build();
        token.changeUser(user);
        return user;
    }

    public void withdrawUser() {
        this.profileImageUrl = null;
        this.nickname = null;
        this.fcmToken.updateFcmToken(null);
        this.platformId = null;
        this.refreshToken = null;
        this.platform = Platform.WITHDRAW;
    }

    public void changeFcmToken(FcmToken fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
