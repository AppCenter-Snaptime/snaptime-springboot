package me.snaptime.report.domain.repository;

import me.snaptime.report.domain.entity.Report;
import me.snaptime.report.domain.entity.ReportStatus;
import me.snaptime.report.domain.entity.ReportType;
import me.snaptime.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {
    boolean existsByReporterAndTargetIdAndReportType(User reporter, Long targetId, ReportType reportType);
    Page<Report> findByReportStatus(ReportStatus reportStatus, Pageable pageable);
}
