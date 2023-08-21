package org.baggle.domain.meeting.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String memo;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime meetingTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime certificationTime;
    private List<ParticipationDetailResponseDto> members;

    @Builder
    public MeetingDetailResponseDto(Long meetingId, String title, String place, String memo, String status, LocalDateTime meetingTime, LocalDateTime certificationTime, List<ParticipationDetailResponseDto> members) {
        this.meetingId = meetingId;
        this.title = title;
        this.place = place;
        this.memo = memo;
        this.status = status;
        this.meetingTime = meetingTime;
        this.certificationTime = certificationTime;
        this.members = members;
    }

    public static MeetingDetailResponseDto of(Meeting meeting, LocalDateTime meetingTime, LocalDateTime certificationTime, List<ParticipationDetailResponseDto> participationDetailResponseDto) {
        return MeetingDetailResponseDto.builder()
                .meetingId(meeting.getId())
                .title(meeting.getTitle())
                .place(meeting.getPlace())
                .memo(meeting.getMemo())
                .status(meeting.getMeetingStatus().name())
                .meetingTime(meetingTime)
                .certificationTime(certificationTime)
                .members(participationDetailResponseDto)
                .build();
    }
}
