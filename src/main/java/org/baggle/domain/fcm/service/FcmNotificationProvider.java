package org.baggle.domain.fcm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Component
public class FcmNotificationProvider {
    private final MessageSource messageSource;

    public String getEmergencyNotificationTitle() {
        return messageSource.getMessage("emergency.notification.title", null, Locale.KOREA);
    }

    public String getEmergencyNotificationBody() {
        return messageSource.getMessage("emergency.notification.body", null, Locale.KOREA);
    }

    public String getConfirmationNotificationTitle() {
        return messageSource.getMessage("confirmation.notification.title", null, Locale.KOREA);
    }

    public String getConfirmationNotificationBody() {
        return messageSource.getMessage("confirmation.notification.body", null, Locale.KOREA);
    }

    public String getButtonOwnerNotificationTitle() {
        return messageSource.getMessage("button.owner.notification.title", null, Locale.KOREA);
    }

    public String getButtonOwnerNotificationBody() {
        return messageSource.getMessage("button.owner.notification.body", null, Locale.KOREA);
    }

    public String getTerminationNotificationTitle() {
        return messageSource.getMessage("termination.notification.title", null, Locale.KOREA);
    }

    public String getTerminationNotificationBody() {
        return messageSource.getMessage("termination.notification.body", null, Locale.KOREA);
    }

    public String getDeleteNotificationTitle() {
        return messageSource.getMessage("delete.notification.title", null, Locale.KOREA);
    }

    public String getDeleteNotificationBody(String name){
        return messageSource.getMessage("delete.notification.body", new String[]{name}, Locale.KOREA);
    }

    public String getDeleteMeetingNotificationBody(){
        return messageSource.getMessage("delete.meeting.notification.body", new String[]{}, Locale.KOREA);
    }
}
