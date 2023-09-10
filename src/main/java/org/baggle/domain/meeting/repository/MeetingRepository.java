package org.baggle.domain.meeting.repository;

import org.baggle.domain.meeting.domain.Meeting;
import org.baggle.domain.meeting.domain.MeetingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("SELECT m " +
            "FROM Meeting m " +
            "WHERE m.meetingStatus = :meetingStatus " +
            "AND STR_TO_DATE(CONCAT(m.date, ' ', m.time), '%Y-%m-%d %H:%i:%s') BETWEEN :from AND :to ")
    List<Meeting> findMeetingsWithinTimeRangeAlongMeetingStatus(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("meetingStatus") MeetingStatus meetingStatus);

    @Query("SELECT m " +
            "FROM Meeting m " +
            "JOIN Participation p " +
            "ON m = p.meeting " +
            "JOIN User u " +
            "ON p.user = u " +
            "WHERE STR_TO_DATE(CONCAT(m.date, ' ', m.time), '%Y-%m-%d %H:%i:%s') BETWEEN :from AND :to " +
            "AND u.id = :userId")
    List<Meeting> findMeetingsWithinTimeRange(@Param("userId") Long userId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT count(m) " +
            "FROM Meeting m " +
            "JOIN Participation p " +
            "ON m = p.meeting " +
            "JOIN User u " +
            "ON p.user = u " +
            "WHERE m.date = :date " +
            "AND u.id = :userId")
    Long countMeetingWithinDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT new org.baggle.domain.meeting.repository.MeetingCountQueryDto(SUM(CASE WHEN m.meetingStatus != :meetingStatus THEN 1 END), SUM(CASE WHEN m.meetingStatus = :meetingStatus THEN 1 END)) " +
            "FROM Meeting m " +
            "JOIN Participation p " +
            "ON p.meeting = m " +
            "JOIN User u " +
            "ON p.user = u " +
            "WHERE u.id = :userId")
    Optional<MeetingCountQueryDto> countMeetings(@Param("meetingStatus") MeetingStatus meetingStatus, @Param("userId") Long userId);

    @Query("SELECT m " +
            "FROM Meeting m " +
            "JOIN Participation p " +
            "ON m = p.meeting " +
            "JOIN User u " +
            "ON p.user = u " +
            "WHERE u.id = :userId " +
            "AND m.meetingStatus != :meetingStatus " +
            "ORDER BY DATEDIFF(m.date, CAST(STR_TO_DATE(:currDate, '%Y-%m-%d') AS DATE)), REPLACE(CAST(TIMEDIFF(STR_TO_DATE(CONCAT(m.date, ' ', m.time), '%Y-%m-%d %H:%i:%s'), STR_TO_DATE(CONCAT(:currDate, ' ', :currTime), '%Y-%m-%d %H:%i:%s')) AS STRING), ':', '')")
    Page<Meeting> findMeetingsWithoutMeetingStatus(@Param("userId") Long userId, @Param("meetingStatus") MeetingStatus meetingStatus, @Param("currDate") String currDate, @Param("currTime") String currTime, Pageable pageable);

    @Query("SELECT m " +
            "FROM Meeting m " +
            "JOIN Participation p " +
            "ON m = p.meeting " +
            "JOIN User u " +
            "ON p.user = u " +
            "WHERE u.id = :userId " +
            "AND m.meetingStatus = :meetingStatus " +
            "ORDER BY DATEDIFF(CAST(STR_TO_DATE(:currDate, '%Y-%m-%d') AS DATE), m.date), REPLACE(CAST(TIMEDIFF(STR_TO_DATE(CONCAT(:currDate, ' ', :currTime), '%Y-%m-%d %H:%i:%s'), STR_TO_DATE(CONCAT(m.date, ' ', m.time), '%Y-%m-%d %H:%i:%s')) AS STRING), ':', '')")
    Page<Meeting> findMeetingsWithMeetingStatus(@Param("userId") Long userId, @Param("meetingStatus") MeetingStatus meetingStatus, @Param("currDate") String currDate, @Param("currTime") String currTime, Pageable pageable);
}
