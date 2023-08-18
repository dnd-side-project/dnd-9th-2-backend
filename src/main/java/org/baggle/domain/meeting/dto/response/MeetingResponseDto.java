package org.baggle.domain.meeting.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.Participation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MeetingResponseDto {
    private int remainingDate;
    private String title;
    private String place;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime time;
    private int participantCount;
    private String status;
    private List<ParticipantResponseDto> participants;

    @Builder
    private MeetingResponseDto(int remainingDate, String title, String place, LocalDateTime time, int participantCount, String status, List<Participation> participations) {
        this.remainingDate = remainingDate;
        this.title = title;
        this.place = place;
        this.time = time;
        this.participantCount = participantCount;
        this.status = status;
        this.participants = participations.stream()
                .map(participation -> ParticipantResponseDto.of(participation.getUser()))
                .collect(Collectors.toList());
    }

    public static MeetingResponseDto of(Meeting meeting) {
        return MeetingResponseDto.builder()
                .remainingDate(Period.between(LocalDate.now(), meeting.getDate()).getDays())
                .title(meeting.getTitle())
                .place(meeting.getTitle())
                .time(LocalDateTime.of(meeting.getDate(), meeting.getTime()))
                .participantCount(meeting.getParticipations().size())
                .status(meeting.getMeetingStatus().name())
                .participations(meeting.getParticipations())
                .build();
    }
}
