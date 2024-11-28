package me.snaptime.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.reply.domain.ChildReply;
import me.snaptime.reply.domain.ParentReply;
import me.snaptime.reply.repository.ChildReplyRepository;
import me.snaptime.reply.repository.ParentReplyRepository;
import me.snaptime.report.domain.dto.PagingReportInfo;
import me.snaptime.report.domain.dto.ReportInfo;
import me.snaptime.report.domain.entity.Report;
import me.snaptime.report.domain.entity.ReportReason;
import me.snaptime.report.domain.entity.ReportStatus;
import me.snaptime.report.domain.entity.ReportType;
import me.snaptime.report.domain.repository.ReportRepository;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final SnapRepository snapRepository;
    private final ChildReplyRepository childReplyRepository;
    private final ParentReplyRepository parentReplyRepository;

    public ReportInfo createReport(String reqEmail, Long targetId, ReportType reportType, ReportReason reportReason) {
        User reporter = userRepository.findByEmail(reqEmail).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        User targetOwner = findTargetOwner(targetId, reportType);

        // 자기 자신 신고 방지
        if(reporter.equals(targetOwner)){
            throw new CustomException(ExceptionCode.SELF_REPORT_NOT_ALLOWED);
        }

        // 중복 신고 방지
        if(reportRepository.existsByReporterAndTargetIdAndReportType(reporter, targetId, reportType)){
            throw new CustomException(ExceptionCode.DUPLICATE_REPORT);
        }

        Report report = Report.builder()
                .reporter(reporter)
                .targetId(targetId)
                .reportType(reportType)
                .reportReason(reportReason)
                .reportStatus(ReportStatus.PENDING)
                .build();
        reportRepository.save(report);

        return ReportInfo.from(report);
    }

    private User findTargetOwner(Long targetId, ReportType reportType){
        if(reportType.equals(ReportType.SNAP)){
            Snap targetSnap = snapRepository.findById(targetId).orElseThrow(()->new CustomException(ExceptionCode.SNAP_NOT_EXIST));
            return targetSnap.getUser();
        }else if(reportType.equals(ReportType.PARENT_REPLY)){
            ParentReply targetParentReply = parentReplyRepository.findById(targetId).orElseThrow(()-> new CustomException(ExceptionCode.REPLY_NOT_FOUND));
            return targetParentReply.getUser();
        }else if(reportType.equals(ReportType.CHILD_REPLY)){
            ChildReply targetChildReply = childReplyRepository.findById(targetId).orElseThrow(()-> new CustomException(ExceptionCode.REPLY_NOT_FOUND));
            return targetChildReply.getUser();
        }

        throw new CustomException(ExceptionCode.USER_NOT_EXIST);
    }

    public PagingReportInfo getReportByStatus(String email, ReportStatus reportStatus, int page) {
        User admin = userRepository.findByEmail(email).orElseThrow(()->new CustomException(ExceptionCode.USER_NOT_EXIST));
        if(!admin.getRoles().get(0).equals("ROLE_ADMIN")){
            throw new CustomException(ExceptionCode.NOT_ADMIN);
        }

        Pageable defaultPageable = PageRequest.of(page, 10); // 기본 페이지 크기 10
        Page<Report> pagingReports = reportRepository.findByReportStatus(reportStatus, defaultPageable);
        List<Report> reports = pagingReports.getContent();
        List<ReportInfo> reportInfos = reports.stream().map(ReportInfo::from).toList();
        long totalPages =  pagingReports.getTotalPages();
        boolean hasNextPage = pagingReports.hasNext();
        boolean hasPreviousPage = pagingReports.hasPrevious();

        return PagingReportInfo.of(reportInfos, totalPages, hasNextPage, hasPreviousPage);
    }

    public ReportInfo changeReportStatus(String email, Long reportId, ReportStatus reportStatus) {
        User admin = userRepository.findByEmail(email).orElseThrow(()->new CustomException(ExceptionCode.USER_NOT_EXIST));
        if(!admin.getRoles().get(0).equals("ROLE_ADMIN")){
            throw new CustomException(ExceptionCode.NOT_ADMIN);
        }

        Report report = reportRepository.findById(reportId).orElseThrow(()-> new CustomException(ExceptionCode.REPORT_NOT_EXIST));

        if(reportStatus != null){
            report.updateStatus(reportStatus);
        }

        return ReportInfo.from(report);
    }
}
