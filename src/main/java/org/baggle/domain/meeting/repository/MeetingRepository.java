package org.baggle.domain.meeting.repository;

import org.baggle.domain.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("SELECT m FROM Meeting m WHERE " +
            "m.date = :currentDate AND m.time between :prevTime and :currentTime ")
    List<Meeting> findMeetingsStartingSoon(@Param(value = "prevTime") LocalTime prevTime, @Param(value = "currentDate") LocalDate currentDate, @Param(value = "currentTime") LocalTime currentTime);

    @Query("SELECT m FROM Meeting m " +
            "JOIN Participation p ON m = p.meeting " +
            "WHERE STR_TO_DATE(CONCAT(m.date, ' ', m.time), '%Y-%m-%d %H:%i:%s') BETWEEN :from AND :to " +
            "AND p.user.id = :userId")
    List<Meeting> findMeetingsWithinTimeRange(@Param("userId") Long userId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT count(m) FROM Meeting m " +
            "JOIN Participation p on m = p.meeting " +
            "WHERE m.date = :date AND p.user.id = :userId")
    Long countMeetingWithinDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
