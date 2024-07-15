package me.snaptime.alarm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/follow/{followAlarmId}")
    @Operation(summary = "팔로우요청 알림에 대해 수락or거절을 합니다.", description =
            "수락 or 거절할 follow알림 id와 수락여부를 보내주세요.<br>" +
            "친구요청 수락(sender(수락자)의 팔로잉 +1, receiver의 팔로워 +1)<br>" +
            "친구요청 거절(sender(수락자)의 팔로워 -1, receiver의 팔로잉 -1) ")

    public ResponseEntity<CommonResponseDto<Void>> acceptFollowReq(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long followAlarmId,
            @RequestParam @NotNull(message = "수락여부를 보내주세요.") boolean isAccept) {

        String msg = alarmService.readFollowAlarm(userDetails.getUsername(),followAlarmId,isAccept);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseDto(msg, null));
    }
}
