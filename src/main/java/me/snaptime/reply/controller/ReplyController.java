package me.snaptime.reply.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.reply.dto.req.AddChildReplyReqDto;
import me.snaptime.reply.dto.req.AddParentReplyReqDto;
import me.snaptime.reply.dto.res.FindChildReplyResDto;
import me.snaptime.reply.dto.res.FindParentReplyResDto;
import me.snaptime.reply.service.ReplyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "[Social] Reply API")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/parent-replies")
    @Operation(summary = "댓글 등록요청", description = "댓글을 등록할 snap의 Id와 댓글내용을 보내주세요.")
    public ResponseEntity<CommonResponseDto<Void>> addParentReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestBody @Valid AddParentReplyReqDto addParentReplyReqDto){

        replyService.addParentReply(userDetails.getUsername(), addParentReplyReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto("댓글등록이 성공했습니다.",null));
    }

    @PostMapping("/child-replies")
    @Operation(summary = "대댓글 등록요청", description = "대댓글을 등록할 부모댓글의 Id와 태그할 유저의 writerLoginId,댓글내용을 입력해주세요<br>태그할 유저가 없다면 tagLoginId는 보내지 않아도 됩니다.")
    public ResponseEntity<CommonResponseDto<Void>> addChildReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestBody @Valid AddChildReplyReqDto addChildReplyReqDto){

        replyService.addChildReply(userDetails.getUsername(), addChildReplyReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto("대댓글등록이 성공했습니다.",null));
    }

    @GetMapping("/parent-replies/{pageNum}")
    @Operation(summary = "댓글 조회요청", description = "댓글조회할 snapId와 페이지번호를 입력해주세요<br>댓글을 20개씩 반환합니다.")
    @Parameters({
            @Parameter(name = "pageNum", description = "페이지번호", required = true, example = "1"),
            @Parameter(name = "snapId", description = "조회할 snapId", required = true, example = "1"),
    })
    public ResponseEntity<CommonResponseDto<FindParentReplyResDto>> readParentReply(
            @RequestParam @NotNull(message = "댓글을 조회할 snapId를 입력해주세요.") final Long snapId,
            @PathVariable final Long pageNum){

        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseDto("댓글조회 성공했습니다.",
                replyService.readParentReply(snapId,pageNum)));
    }

    @GetMapping("/child-replies/{pageNum}")
    @Operation(summary = "대댓글 조회요청", description = "대댓글조회할 부모댓글의 Id와 페이지번호를 입력해주세요<br>대댓글을 20개씩 반환합니다.")
    @Parameters({
            @Parameter(name = "pageNum", description = "페이지번호", required = true, example = "1"),
            @Parameter(name = "parentReplyId", description = "대댓글을 조회할 부모댓글의Id", required = true, example = "1"),
    })
    public ResponseEntity<CommonResponseDto<FindChildReplyResDto>> readChildReply(
            @RequestParam @NotNull(message = "부모댓글의 Id를 입력해주세요.") final Long parentReplyId,
            @PathVariable final Long pageNum){

        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseDto("대댓글조회 성공했습니다.",
                replyService.readChildReply(parentReplyId,pageNum)));
    }

    @PatchMapping("/parent-replies/{parentReplyId}")
    @Operation(summary = "댓글 수정요청", description = "댓글 ID와 수정할 댓글내용을 입력해주세요")
    @Parameters({
            @Parameter(name = "parentReplyId", description = "댓글ID", required = true, example = "1"),
            @Parameter(name = "newContent", description = "수정할 댓글내용", required = true, example = "수정된 댓글"),
    })
    public ResponseEntity<CommonResponseDto<Void>> updateParentReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long parentReplyId,
            @RequestParam final String newContent){

        replyService.updateParentReply(userDetails.getUsername(),parentReplyId,newContent);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("댓글 수정을 완료했습니다",null));
    }

    @PatchMapping("/child-replies/{childReplyId}")
    @Operation(summary = "대댓글 수정요청", description = "대댓글 ID와 수정할 댓글내용을 입력해주세요")
    @Parameters({
            @Parameter(name = "childReplyId", description = "대댓글ID", required = true, example = "1"),
            @Parameter(name = "newContent", description = "수정할 대댓글내용", required = true, example = "수정된 대댓글"),
    })
    public ResponseEntity<CommonResponseDto<Void>> updateChildReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long childReplyId,
            @RequestParam final String newContent){

        replyService.updateChildReply(userDetails.getUsername(),childReplyId,newContent);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("대댓글 수정을 완료했습니다",null));
    }

    @DeleteMapping("/parent-replies/{parentReplyId}")
    @Operation(summary = "댓글 삭제요청", description = "삭제할 댓글 ID를 입력해주세요")
    @Parameters({
            @Parameter(name = "parentReplyId", description = "댓글ID", required = true, example = "1")
    })
    public ResponseEntity<CommonResponseDto<Void>> deleteParentReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long parentReplyId){

        replyService.deleteParentReply(userDetails.getUsername(),parentReplyId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("댓글 삭제를 완료했습니다",null));
    }

    @DeleteMapping("/child-replies/{childReplyId}")
    @Operation(summary = "대댓글 삭제요청", description = "삭제할 대댓글 ID를 입력해주세요")
    @Parameters({
            @Parameter(name = "childReplyId", description = "대댓글ID", required = true, example = "1")
    })
    public ResponseEntity<CommonResponseDto<Void>> deleteChildReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long childReplyId){

        replyService.deleteChildReply(userDetails.getUsername(),childReplyId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("대댓글 삭제를 완료했습니다",null));
    }
}
