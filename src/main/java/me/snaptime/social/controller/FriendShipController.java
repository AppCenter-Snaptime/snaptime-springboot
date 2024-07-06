package me.snaptime.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.social.common.FriendSearchType;
import me.snaptime.social.data.dto.req.AcceptFollowReqDto;
import me.snaptime.social.data.dto.res.FindFriendResDto;
import me.snaptime.social.service.FriendShipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/friends")
@RequiredArgsConstructor
@Tag(name = "[Social] Friend API")
public class FriendShipController {

    private final FriendShipService friendShipService;

    @PostMapping
    @Operation(summary = "팔로우 요청", description = "팔로우할 유저의 loginId를 입력해주세요.<br>fromUser(요청자)의 팔로잉 +1, toUser의 팔로워 +1")
    @Parameter(name = "fromUserLoginId", description = "팔로우할 유저의 loginId", required = true, example = "seyong")
    public ResponseEntity<CommonResponseDto<Void>> sendFollowReq(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "fromUserLoginId") @NotBlank(message = "팔로우요청을 보낼 유저의 이름을 입력해주세요.")String fromUserLoginId) {

        String loginId = userDetails.getUsername();
        friendShipService.sendFriendShipReq(loginId,fromUserLoginId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseDto("팔로우가 완료되었습니다.", null));
    }

    @PostMapping("/accept")
    @Operation(summary = "팔로우 수락or거절 요청", description = "팔로우요청을 수락or거절할 유저의 이름을 입력해주세요.<br>친구요청 수락(fromUser(수락자)의 팔로잉 +1, toUser의 팔로워 +1)<br>친구요청 거절(fromUser(수락자)의 팔로워 -1, toUser의 팔로잉 -1) ")
    public ResponseEntity<CommonResponseDto<Void>> acceptFollowReq(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AcceptFollowReqDto acceptFollowReqDto) {

        String loginId = userDetails.getUsername();
        String msg = friendShipService.acceptFriendShipReq(loginId, acceptFollowReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseDto(msg, null));
    }

    @DeleteMapping("/{friendShipId}")
    @Operation(summary = "팔로우하는 친구삭제", description = "팔로우요청을 수락or거절할 유저의 이름을 입력해주세요.<br>fromUser(삭제자)의 팔로잉 -1, toUser의 팔로워 -1")
    @Parameter(name = "friendShipId", description = "팔로우 삭제할 친구관계 id", required = true, example = "1")
    public ResponseEntity<CommonResponseDto<Void>> deleteFollow(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable final Long friendShipId) {

        String loginId = userDetails.getUsername();
        friendShipService.deleteFriendShip(loginId,friendShipId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("팔로우삭제가 완료되었습니다.", null));
    }

    @GetMapping("/{pageNum}")
    @Operation(summary = "팔로워/팔로잉 친구목록 조회(20개씩 반환)",
            description = "팔로워와 팔로잉중 어느 친구목록을 조회할 것인지 + 검색키워드 정보를 입력해주세요." +
                    "<br>검색키워드는 필수가 아니며 없으면 입력하지 않아도 됩니다." +
                    "<br>스냅에 친구를 태그하기 위해 태그할 유저 조회를 할 경우 friendSearchType을 팔로잉으로 보내시면 됩니다.")
    @Parameters({
            @Parameter(name = "loginId" , description = "친구목록을 조회할 유저의 loginId", required = true,example = "tempLoginId"),
            @Parameter(name = "searchKeyword", description = "친구 검색키워드", required = false, example = "홍길동"),
            @Parameter(name = "friendSearchType", description = "검색 타입(팔로워 조회 시 FOLLOWER/팔로잉 조회 시 FOLLOWING)으로 입력해주세요.", required = true, example = "FOLLOWER"),
            @Parameter(name = "pageNum", description = "친구조회 페이지번호", required = true, example = "1")
    })
    public ResponseEntity<CommonResponseDto<FindFriendResDto>> findFriendList(
            @RequestParam(name = "loginId") @NotBlank(message = "친구목록을 조회할 유저의 loginId를 입력해주세요.") String loginId,
            @RequestParam(name = "friendSearchType") @NotNull(message = "팔로우,팔로잉중 하나를 입력해주세요.") FriendSearchType friendSearchType,
            @RequestParam(name = "searchKeyword",required = false) String searchKeyword,
            @PathVariable(name = "pageNum") final Long pageNum){

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("친구조회가 완료되었습니다.",
                friendShipService.findFriendList(loginId,pageNum,friendSearchType,searchKeyword)));
    }

}
