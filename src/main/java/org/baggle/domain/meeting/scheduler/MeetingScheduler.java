package org.baggle.domain.meeting.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.MeetingStatus;
import org.baggle.domain.meeting.service.MeetingDetailService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableAsync
@EnableScheduling
public class MeetingScheduler {
    private final MeetingDetailService meetingDetailService;

    @Transactional
    @Scheduled(cron = "30 * * * * *")
    public void meetingScheduleTask() {
        List<Meeting> meetingList = getMeetingList();
        updateMeetingListStatus(meetingList);
    }

    private void updateMeetingListStatus(List<Meeting> meetingList) {
        meetingList.forEach(meeting -> {
            meeting.updateMeetingStatusInto(MeetingStatus.PAST);
            log.info("Termination meeting information - date {}, time {}", meeting.getDate(), meeting.getTime());
        });
    }

    private List<Meeting> getMeetingList() {
        return meetingDetailService.findMeetingsInRange(-241, -240, MeetingStatus.TERMINATION);
    }
}
