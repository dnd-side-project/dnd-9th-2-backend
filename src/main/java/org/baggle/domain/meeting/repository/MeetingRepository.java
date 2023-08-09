package org.baggle.domain.meeting.repository;

import org.baggle.domain.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("SELECT m FROM Meeting m WHERE " +
            "m.date = :currentDate AND m.time between :prevTime and :currentTime ")
    List<Meeting> findMeetingsStartingSoon(@Param(value = "prevTime") LocalTime prevTime, @Param(value = "currentDate") LocalDate currentDate, @Param(value = "currentTime") LocalTime currentTime);
}
