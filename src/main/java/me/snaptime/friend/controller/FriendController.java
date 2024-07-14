package me.snaptime.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.friend.dto.req.AcceptFollowReqDto;
import me.snaptime.friend.dto.res.FindFriendResDto;
import me.snaptime.friend.service.FriendService;
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
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    @Operation(summary = "팔로우 요청", description = "팔로우할 유저의 loginId를 입력해주세요.<br>sender의 팔로잉 +1, receiver의 팔로워 +1")
    @Parameter(name = "receiverLoginId", description = "팔로우할 유저의 loginId를 입력해주세요", required = true, example = "seyong")
    public ResponseEntity<CommonResponseDto<Void>> sendFollowReq(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "receiverLoginId") @NotBlank(message = "팔로우요청을 보낼 유저의 이름을 입력해주세요.")String receiverLoginId) {

        friendService.sendFollow(userDetails.getUsername(), receiverLoginId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseDto("팔로우가 완료되었습니다.", null));
    }

    @PostMapping("/accept")
    @Operation(summary = "팔로우 수락or거절 요청", description = "팔로우요청을 수락or거절할 유저의 이름을 입력해주세요.<br>친구요청 수락(sender(수락자)의 팔로잉 +1, receiver의 팔로워 +1)<br>친구요청 거절(sender(수락자)의 팔로워 -1, receiver의 팔로잉 -1) ")
    public ResponseEntity<CommonResponseDto<Void>> acceptFollowReq(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AcceptFollowReqDto acceptFollowReqDto) {

        String msg = friendService.acceptFollow(userDetails.getUsername(), acceptFollowReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseDto(msg, null));
    }

    @DeleteMapping
    @Operation(summary = "팔로우하는 친구삭제", description = "언팔로우 할 친구의 loginId를 보내주세요.<br>deletor(언팔하는 유저)의 팔로잉 -1, deletedUser(언팔당하는 유저)의 팔로워 -1")
    @Parameter(name = "deletedUserLoginId", description = "팔로우 삭제할 친구의 loginId를 입력해주세요.", required = true, example = "seyong")
    public ResponseEntity<CommonResponseDto<Void>> deleteFollow(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @NotBlank(message = "언팔로우할 유저의 loginId를 입력해주세요.")final String deletedUserLoginId) {

        friendService.unFollow(userDetails.getUsername(), deletedUserLoginId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("팔로우삭제가 완료되었습니다.", null));
    }

    @GetMapping("/{pageNum}")
    @Operation(summary = "팔로워/팔로잉 친구목록 조회(20개씩 반환)",
            description = "팔로워와 팔로잉중 어느 친구목록을 조회할 것인지 + 검색키워드 정보를 입력해주세요." +
                    "<br>검색키워드는 필수가 아니며 없으면 입력하지 않아도 됩니다." +
                    "<br>스냅에 친구를 태그하기 위해 태그할 유저 조회를 할 경우 friendSearchType을 팔로잉으로 보내시면 됩니다.")
    @Parameters({
            @Parameter(name = "targetLoginId" , description = "친구목록을 조회할 유저의 loginId", required = true,example = "tempLoginId"),
            @Parameter(name = "searchKeyword", description = "친구 검색키워드", required = false, example = "홍길동"),
            @Parameter(name = "friendSearchType", description = "검색 타입(팔로워 조회 시 FOLLOWER/팔로잉 조회 시 FOLLOWING)으로 입력해주세요.", required = true, example = "FOLLOWER"),
            @Parameter(name = "pageNum", description = "친구조회 페이지번호", required = true, example = "1")
    })
    public ResponseEntity<CommonResponseDto<FindFriendResDto>> findFriendList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "targetLoginId") @NotBlank(message = "친구목록을 조회할 유저의 loginId를 입력해주세요.") String loginId,
            @RequestParam(name = "friendSearchType") @NotNull(message = "팔로우,팔로잉중 하나를 입력해주세요.") FriendSearchType friendSearchType,
            @RequestParam(name = "searchKeyword",required = false) String searchKeyword,
            @PathVariable(name = "pageNum") final Long pageNum){

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("친구조회가 완료되었습니다.",
                friendService.findFriendList(userDetails.getUsername(), loginId,pageNum,friendSearchType,searchKeyword)));
    }

}
