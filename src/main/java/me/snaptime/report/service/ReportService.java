package me.snaptime.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.reply.domain.ChildReply;
import me.snaptime.reply.domain.ParentReply;
import me.snaptime.reply.repository.ChildReplyRepository;
import me.snaptime.reply.repository.ParentReplyRepository;
import me.snaptime.report.dto.PagingReportInfo;
import me.snaptime.report.dto.ReportInfo;
import me.snaptime.report.domain.Report;
import me.snaptime.report.domain.enums.ReportReason;
import me.snaptime.report.domain.enums.ReportStatus;
import me.snaptime.report.domain.enums.ReportType;
import me.snaptime.report.repository.ReportRepository;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Transactional(readOnly = true)
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

    public String processReportByStatus(String email, Long targetId, int penaltyPoint) {
        User admin = userRepository.findByEmail(email).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));
        // admin 계정인지 체크합니다.
        if(!admin.getRoles().get(0).equals("ROLE_ADMIN")){
            throw new CustomException(ExceptionCode.NOT_ADMIN);
        }
        Report report = reportRepository.findById(targetId).orElseThrow(()-> new CustomException(ExceptionCode.REPORT_NOT_EXIST));
        ReportStatus reportStatus = report.getReportStatus();
        ReportType reportType = report.getReportType();

        // 승인된 신고이면 해당 snap, reply 삭제 후 유저 패널티를 부여 후, 신고를 삭제합니다.
        if(reportStatus.equals(ReportStatus.APPROVED)){
            handleApprovedReport(reportType, report.getTargetId(),penaltyPoint);
            reportRepository.delete(report);
            return "신고가 승인되어 대상이 삭제되고, 신고 정보가 제거되었습니다.";
        // 거절된 신고이면 신고만 삭제합니다.
        } else if(reportStatus.equals(ReportStatus.REJECTED)){
            reportRepository.delete(report);
            return "신고가 거절되어 신고 정보가 제거되었습니다.";
        }

        throw new CustomException(ExceptionCode.INVALID_REPORT_STATUS);
    }

    private void handleApprovedReport(ReportType reportType, Long targetId, int penaltyPoint) {
        User targetOwner = null;

        // 신고된 snap, parentReply, childReply 조회 후  사용자를 찾아냅니다.
        if (reportType == ReportType.SNAP) {
            Snap snap = snapRepository.findById(targetId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
            targetOwner = snap.getUser();
            snapRepository.delete(snap);
            log.info("Snap(ID: {})이 삭제되었습니다.", targetId);
        } else if (reportType == ReportType.PARENT_REPLY) {
            ParentReply parentReply = parentReplyRepository.findById(targetId).orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));
            targetOwner = parentReply.getUser();
            parentReplyRepository.delete(parentReply);
            log.info("ParentReply(ID: {})가 삭제되었습니다.", targetId);
        } else if (reportType == ReportType.CHILD_REPLY) {
            ChildReply childReply = childReplyRepository.findById(targetId).orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));
            targetOwner = childReply.getUser();
            childReplyRepository.delete(childReply);
            log.info("ChildReply(ID: {})가 삭제되었습니다.", targetId);
        }else{
            throw new CustomException(ExceptionCode.INVALID_REPORT_TYPE);
        }

        // 소유자에게 패널티를 부여합니다.
        if (targetOwner != null) {
            targetOwner.addPenalty(penaltyPoint);
            if(targetOwner.getPenalty()>=5){
                targetOwner.updateBenUserAuth();
                targetOwner.clearPenalty();
                targetOwner.setBanEndTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusSeconds(50));
            }
        }
    }

    //@Scheduled(cron = "*/30 * * * * *") // 매 30초마다 실행
    @Scheduled(cron = "0 0 5 * * *") // 매일 새벽 5시 실행
    public void restoreUserRoles() {
        log.info("ROLE_BEN 사용자 상태 복원 작업 시작");

        // banEndTime이 지난 유저 검색
        List<User> bannedUsers = userRepository.findByRoles("ROLE_BEN");

        for (User user : bannedUsers) {
            log.info("BenUser : {}", user.getRoles());
            if(LocalDateTime.now(ZoneId.of("Asia/Seoul")).isAfter(user.getBanEndTime())){
                user.restoreRole(); // ROLE_USER로 복원
                log.info("사용자 {}의 역할이 ROLE_USER로 복원되었습니다.", user.getEmail());
            }
        }
        log.info("ROLE_BEN 사용자 상태 복원 작업 완료");
    }
}
