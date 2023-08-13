package org.baggle.domain.meeting.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.meeting.domain.Meeting;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MeetingDetailResponseDto {
    private Long meetingId;
    private String title;
    private String place;
    private String date;
    private String time;
    private String memo;
    private LocalDateTime certificationTime;
    private List<ParticipationDetailResponseDto> members;

    @Builder
    public MeetingDetailResponseDto(Long meetingId, String title, String place, String date, String time, String memo, LocalDateTime certificationTime, List<ParticipationDetailResponseDto> members) {
        this.meetingId = meetingId;
        this.title = title;
        this.place = place;
        this.date = date;
        this.time = time;
        this.memo = memo;
        this.certificationTime = certificationTime;
        this.members = members;
    }

    public static MeetingDetailResponseDto of(Meeting meeting, LocalDateTime certificationTime, List<ParticipationDetailResponseDto> participationDetailResponseDto) {
        return MeetingDetailResponseDto.builder()
                .meetingId(meeting.getId())
                .title(meeting.getTitle())
                .place(meeting.getPlace())
                .date(meeting.getDate().toString())
                .time(meeting.getTime().toString())
                .memo(meeting.getMemo())
                .certificationTime(certificationTime)
                .members(participationDetailResponseDto)
                .build();
    }
}
