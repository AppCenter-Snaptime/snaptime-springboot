package me.snaptime.social.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.social.data.dto.req.AcceptFollowReqDto;
import me.snaptime.social.service.FriendShipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/friends")
@RequiredArgsConstructor
@Tag(name = "[Social] Friend API")
public class FriendShipController {

    private final FriendShipService friendShipService;
    private String tmpLoginId = "tempString";

    @PostMapping
    @Operation(summary = "팔로우 요청", description = "팔로우할 유저의 이름을 입력해주세요.")
    @Parameter(name = "fromUserName", description = "팔로우할 유저의 이름", required = true, example = "seyong")
    public ResponseEntity<CommonResponseDto> sendFollowReq(
            @RequestParam(name = "fromUserName") @NotBlank(message = "팔로우요청을 보낼 유저의 이름을 입력해주세요.")String fromUserName) {

        // 토큰에서 loginId추출하는 로직
        String loginId = tmpLoginId;
        friendShipService.sendFriendShipReq(loginId,fromUserName);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseDto("팔로우가 완료되었습니다.", null));
    }

    @PostMapping("/accept")
    @Operation(summary = "팔로우 수락or거절 요청", description = "팔로우요청을 수락or거절할 유저의 이름을 입력해주세요.")
    public ResponseEntity<CommonResponseDto> acceptFollowReq(@RequestBody @Valid AcceptFollowReqDto acceptFollowReqDto) {

        // 토큰에서 loginId추출하는 로직
        String loginId = tmpLoginId;
        String msg = friendShipService.acceptFriendShipReq(loginId, acceptFollowReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseDto(msg, null));
    }
}
