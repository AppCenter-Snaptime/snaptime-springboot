package me.snaptime.report.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.report.domain.dto.PagingReportInfo;
import me.snaptime.report.domain.dto.ReportInfo;
import me.snaptime.report.domain.entity.ReportReason;
import me.snaptime.report.domain.entity.ReportStatus;
import me.snaptime.report.domain.entity.ReportType;
import me.snaptime.report.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[Report] Report API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;


    @Operation(summary = "게시물/댓글 을 신고합니다.", description = "게시글/댓글 ID와 신고 사유, 신고 대상 타입(SNAP, CHILD_REPLY, PARENT_REPLY 를 입력해주세요.")
    @PostMapping("/create/{targetId}")
    public ResponseEntity<CommonResponseDto<ReportInfo>> createReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("targetId")Long targetId,
            ReportReason reason,
            ReportType type){
        log.info("report Id : {}, report type : {} report Reason : {}", targetId, type, reason);
        String reqEmail = userDetails.getUsername();
        ReportInfo reportInfo = reportService.createReport(reqEmail,targetId,type,reason);

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto<>(
                type + " 신고를 성공적으로 완료하였습니다.",
                reportInfo
        ));
    }
    @Operation(summary = "신고 내역 조회", description = "관리자의 권한으로 상태에 맞는 신고 내역을 조회합니다. + <br><br> +" +
            "PENDING(대기중), UNDER_REVIEW(검토중), REJECT(신고가 반려된 상태), APPROVE(신고가 승인되어 조치 하면 되는 상태)")
    @GetMapping("/admin")
    public ResponseEntity<CommonResponseDto<PagingReportInfo>> getReportByStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false, defaultValue = "0") int page,
            ReportStatus reportStatus){
        log.info("Report status : {}", reportStatus);
        String email = userDetails.getUsername();
        PagingReportInfo pagingReportInfo = reportService.getReportByStatus(email, reportStatus, page);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto<>(
                "성공적으로 " + reportStatus + "상태에 해당하는 신고를 조회하였습니다.",
                pagingReportInfo
        ));
    }

    @Operation(summary = "신고 상태 변경", description = "관리자의 권한으로 신고 상태를 변경합니다.")
    @PatchMapping("/admin/update/{reportId}")
    public ResponseEntity<CommonResponseDto<ReportInfo>> changeReportStatus(
            @PathVariable("reportId") Long reportId,
            @AuthenticationPrincipal UserDetails userDetails,
            ReportStatus reportStatus){
        log.info("Report status : {}", reportStatus);
        String email = userDetails.getUsername();
        ReportInfo reportInfo = reportService.changeReportStatus(email,reportId,reportStatus);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto<>(
                "성공적으로 신고 상태를 변경하였습니다.",
                reportInfo
        ));
    }

}
