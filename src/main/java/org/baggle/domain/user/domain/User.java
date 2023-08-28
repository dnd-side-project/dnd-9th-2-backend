package org.baggle.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.baggle.domain.fcm.domain.FcmToken;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.global.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

import static org.baggle.domain.fcm.domain.FcmToken.createFcmToken;

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
    @Builder.Default
    private List<Participation> participations = new ArrayList<>();
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private FcmToken fcmToken;
    @Enumerated(value = EnumType.STRING)
    private Platform platform;
    private String platformId;
    private String refreshToken;

    public static User createUser(String profileImageUrl, String nickname, String fcmToken, Platform platform, String platformId) {
        User user = User.builder()
                .profileImageUrl(profileImageUrl)
                .nickname(nickname)
                .platform(platform)
                .platformId(platformId)
                .build();
        FcmToken token = createFcmToken(fcmToken);
        token.changeUser(user);
        return user;
    }

    public void withdrawUser() {
        this.profileImageUrl = null;
        this.nickname = null;
        this.fcmToken.updateFcmToken(null);
        this.platformId = null;
        this.platform = Platform.WITHDRAW;
    }

    public void updateFcmToken(FcmToken fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}