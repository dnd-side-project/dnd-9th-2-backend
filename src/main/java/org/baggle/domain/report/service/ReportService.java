package org.baggle.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.baggle.domain.feed.domain.Feed;
import org.baggle.domain.feed.repository.FeedRepository;
import org.baggle.domain.meeting.domain.Participation;
import org.baggle.domain.meeting.repository.ParticipationRepository;
import org.baggle.domain.report.domain.Report;
import org.baggle.domain.report.domain.ReportType;
import org.baggle.domain.report.dto.request.CreateReportRequestDto;
import org.baggle.domain.report.repository.ReportRepository;
import org.baggle.global.error.exception.ConflictException;
import org.baggle.global.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.baggle.domain.report.domain.ReportType.getEnumReportTypeFromStringReportType;
import static org.baggle.global.error.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional
@Service
public class ReportService {
    private final FeedRepository feedRepository;
    private final ParticipationRepository participationRepository;
    private final ReportRepository reportRepository;

    public void createReport(CreateReportRequestDto requestDto) {
        ReportType enumReportType = getEnumReportTypeFromStringReportType(requestDto.getReportType());
        Feed findFeed = getFeedWithId(requestDto.getFeedId());
        Participation findParticipation = getParticipationWithId(requestDto.getMemberId());
        validateDuplicateReport(findFeed, findParticipation);
        Report createdReport = Report.createReport(findFeed, findParticipation, enumReportType);
        saveReport(createdReport);
    }

    private void validateDuplicateReport(Feed feed, Participation participation) {
        if (reportRepository.existsReportByFeedIdAndParticipationId(feed.getId(), participation.getId()))
            throw new ConflictException(DUPLICATE_REPORT);
    }

    private void saveReport(Report report) {
        reportRepository.save(report);
    }

    private Feed getFeedWithId(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException(FEED_NOT_FOUND));
    }

    private Participation getParticipationWithId(Long participationId) {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException(PARTICIPATION_NOT_FOUND));
    }
}
