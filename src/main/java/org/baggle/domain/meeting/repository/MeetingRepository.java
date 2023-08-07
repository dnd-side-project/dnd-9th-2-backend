package org.baggle.domain.meeting.repository;

import org.baggle.domain.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("SELECT m FROM Meeting m WHERE " +
            "m.date = :currentDate AND m.time between :prevTime and :currentTime ")
    List<Meeting> findMeetingsStartingSoon(LocalTime prevTime, LocalDate currentDate, LocalTime currentTime);
}
