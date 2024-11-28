package me.snaptime.report.domain.dto;

import lombok.Builder;
import me.snaptime.report.domain.entity.Report;
import me.snaptime.report.domain.entity.ReportReason;
import me.snaptime.report.domain.entity.ReportStatus;
import me.snaptime.report.domain.entity.ReportType;

@Builder
public record ReportInfo(
        Long reportId,
        Long reporterId,
        Long targetId,
        ReportReason reportReason,
        ReportType reportType,
        ReportStatus reportStatus
) {
    public static ReportInfo from(Report report){
        return ReportInfo.builder()
                .reportId(report.getId())
                .reporterId(report.getReporter().getUserId())
                .targetId(report.getTargetId())
                .reportReason(report.getReportReason())
                .reportType(report.getReportType())
                .reportStatus(report.getReportStatus())
                .build();
    }
}
