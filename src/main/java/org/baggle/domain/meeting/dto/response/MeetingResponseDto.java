package org.baggle.domain.meeting.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.Participation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.Duration.between;
import static org.baggle.global.common.TimeConverter.convertToLocalDateTime;
import static org.baggle.global.common.TimeConverter.convertToStartLocalDateTime;

@Getter
public class MeetingResponseDto {
    private Long meetingId;
    private int remainingDate;
    private String title;
    private String place;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime time;
    private int participantCount;
    private String status;
    private List<String> participants;

    @Builder
    private MeetingResponseDto(Long meetingId, int remainingDate, String title, String place, LocalDateTime time, int participantCount, String status, List<Participation> participations) {
        this.meetingId = meetingId;
        this.remainingDate = remainingDate;
        this.title = title;
        this.place = place;
        this.time = time;
        this.participantCount = participantCount;
        this.status = status;
        this.participants = participations.stream()
                .map(participation ->
                        participation.getUser().getProfileImageUrl() != null ? participation.getUser().getProfileImageUrl() : "")
                .collect(Collectors.toList());
    }

    public static MeetingResponseDto of(Meeting meeting) {
        return MeetingResponseDto.builder()
                .meetingId(meeting.getId())
                .remainingDate((int) between(convertToStartLocalDateTime(LocalDate.now()), convertToStartLocalDateTime(meeting.getDate())).toDays())
                .title(meeting.getTitle())
                .place(meeting.getPlace())
                .time(convertToLocalDateTime(meeting.getDate(), meeting.getTime()))
                .participantCount(meeting.getParticipations().size())
                .status(meeting.getMeetingStatus().name())
                .participations(meeting.getParticipations())
                .build();
    }
}
