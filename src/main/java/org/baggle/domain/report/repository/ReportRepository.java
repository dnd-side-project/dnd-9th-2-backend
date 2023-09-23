package org.baggle.domain.report.repository;

import org.baggle.domain.report.domain.Report;
import org.baggle.domain.user.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsReportByFeedIdAndParticipationId(Long feedID, Long participationId);
}
