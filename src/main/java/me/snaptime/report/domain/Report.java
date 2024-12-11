package me.snaptime.report.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.BaseTimeEntity;
import me.snaptime.report.domain.enums.ReportReason;
import me.snaptime.report.domain.enums.ReportStatus;
import me.snaptime.report.domain.enums.ReportType;
import me.snaptime.user.domain.User;

@Entity
@Table(name = "report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reportReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus reportStatus;

    @Builder
    protected Report(User reporter, ReportType reportType, Long targetId, ReportReason reportReason, ReportStatus reportStatus){
        this.reporter = reporter;
        this.reportType = reportType;
        this.targetId = targetId;
        this.reportReason = reportReason;
        this.reportStatus =reportStatus;
    }

    public void updateStatus(ReportStatus reportStatus){
        this.reportStatus= reportStatus;
    }
}
