package me.snaptime.alarm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.service.AlarmService;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.reply.dto.res.FindParentReplyResDto;
import me.snaptime.snap.dto.res.FindSnapResDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/alarms")
@RequiredArgsConstructor
@Tag(name = "[Alarm] Alarm API")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/follow/{followAlarmId}")
    @Operation(summary = "팔로우요청 알림에 대해 수락or거절을 합니다.", description =
                    "수락 or 거절할 follow알림 id와 수락여부를 보내주세요.<br>" +
                    "친구요청 수락(sender(수락자)의 팔로잉 +1, receiver의 팔로워 +1)<br>" +
                    "친구요청 거절(sender(수락자)의 팔로워 -1, receiver의 팔로잉 -1) ")
    @Parameters({
            @Parameter(name = "followAlarmId" , description = "followAlarmId를 입력해주세요", required = true,example = "1"),
            @Parameter(name = "isAccept", description = "수락여부를 입력해주세요", required = true, example = "true"),
    })
    public ResponseEntity<CommonResponseDto<Void>> acceptFollowReq(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long followAlarmId,
            @RequestParam @NotNull(message = "수락여부를 보내주세요.") boolean isAccept) {

        String msg = alarmService.readFollowAlarm(userDetails.getUsername(),followAlarmId,isAccept);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto(msg, null));
    }

    @GetMapping("/snaps/{snapAlarmId}")
    @Operation(summary = "스냅알림 조회", description = "스냅알림을 읽음처리 후 해당스냅페이지로 이동합니다.")
    @Parameter(name = "snapAlarmId" , description = "snapAlarmId를 입력해주세요", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<FindSnapResDto>> readSnapAlarm(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long snapAlarmId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto("스냅알림 조회 성공",
                        alarmService.readSnapAlarm(userDetails.getUsername(), snapAlarmId)));
    }

    @GetMapping("/replies/{replyAlarmId}")
    @Operation(summary = "댓글알림 조회", description = "댓글알림을 읽음처리 후 해당 댓글페이지 1번으로 이동합니다.")
    @Parameter(name = "replyAlarmId" , description = "replyAlarmId를 입력해주세요", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<FindParentReplyResDto>> readReplyAlarm(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long replyAlarmId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto("댓글알림 조회 성공",
                        alarmService.readSnapAlarm(userDetails.getUsername(), replyAlarmId)));
    }

    @DeleteMapping ("/{alarmId}")
    @Operation(summary = "알림을 삭제합니다.", description =
                    "읽음 여부와 상관없이 알림을 삭제합니다.<br>" +
                    "팔로우알림의 경우 해당요청을 자동거절처리한 뒤 삭제합니다.")
    @Parameters({
            @Parameter(name = "alarmId" , description = "alarmId를 입력해주세요", required = true,example = "1"),
            @Parameter(name = "alarmType", description = "알림타입을 입력해주세요", required = true, example = "REPLY"),
    })
    public ResponseEntity<CommonResponseDto<Void>> deleteAlarm(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long alarmId,
            @RequestParam AlarmType alarmType) {

        alarmService.deleteAlarm(userDetails.getUsername(), alarmId, alarmType);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto("알림 삭제 성공", null));
    }
}
