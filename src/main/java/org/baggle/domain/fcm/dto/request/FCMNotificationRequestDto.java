package org.baggle.domain.fcm.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.baggle.domain.user.domain.User;

import java.util.List;

@Getter
@NoArgsConstructor
public class FCMNotificationRequestDto {
    private List<Long> targetUserIdList;
    private String title;
    private String body;

    @Builder
    public FCMNotificationRequestDto(List<Long> targetUserIdList, String title, String body){
        this.targetUserIdList = targetUserIdList;
        this.title = title;
        this.body = body;
    }
}
