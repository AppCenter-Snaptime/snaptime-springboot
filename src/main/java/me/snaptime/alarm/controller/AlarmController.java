package me.snaptime.alarm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.dto.res.AlarmFindAllResDto;
import me.snaptime.alarm.service.AlarmService;
import me.snaptime.common.CommonResponseDto;
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
@Log4j2
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/follow/{followAlarmId}")
    @Operation(summary = "팔로우알림 조회", description = "팔로우알림을 읽음처리합니다.")
    @Parameter(name = "followAlarmId" , description = "followAlarmId를 입력해주세요", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<Void>> acceptFollowReq(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long followAlarmId) {

        alarmService.readFollowAlarm(userDetails.getUsername(),followAlarmId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto("팔로우 알림조회 성공", null));
    }

    @GetMapping("/snaps/{snapAlarmId}")
    @Operation(summary = "스냅알림 조회", description = "스냅알림을 읽음처리합니다.")
    @Parameter(name = "snapAlarmId" , description = "snapAlarmId를 입력해주세요", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<Void>> readSnapAlarm(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long snapAlarmId) {

        alarmService.readSnapAlarm(userDetails.getUsername(), snapAlarmId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto("스냅알림 조회 성공", null));
    }

    @GetMapping("/replies/{replyAlarmId}")
    @Operation(summary = "댓글알림 조회", description = "댓글알림을 읽음처리합니다.")
    @Parameter(name = "replyAlarmId" , description = "replyAlarmId를 입력해주세요", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<Void>> readReplyAlarm(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long replyAlarmId) {

        alarmService.readReplyAlarm(userDetails.getUsername(), replyAlarmId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto("댓글알림 조회 성공", null));
    }

    @GetMapping("/count/not-read")
    @Operation(summary = "미확인알림개수 조회", description = "확인되지 않은 알림개수를 조회합니다.")
    public ResponseEntity<CommonResponseDto<Long>> findNotReadAlarmCnt(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto("미확인 알림개수 조회성공",
                        alarmService.findNotReadAlarmCnt(userDetails.getUsername())));
    }

    @GetMapping
    @Operation(summary = "알림리스트 조회", description = "자신에게 온 알림리스트를 조회합니다.<br>"+
                                        "읽지않은 알림을 먼저 보여주며 시간순으로 정렬하여 반환합니다.<br>"+
                                        "알림타입별로 반환되는 데이터가 다릅니다.<br>" +
                                        "각 알림타입별로 alarmId값이 부여되기 때문에 타입이 다른 알림의 경우 id값이 중복될 수 있습니다.")
    public ResponseEntity<CommonResponseDto<AlarmFindAllResDto>> findAlarms(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto("알림리스트 조회성공",
                        alarmService.findAlarms(userDetails.getUsername())));
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
