package me.snaptime.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.social.data.dto.req.AddChildReplyReqDto;
import me.snaptime.social.data.dto.res.FindChildReplyResDto;
import me.snaptime.social.data.dto.res.FindParentReplyResDto;
import me.snaptime.social.service.ReplyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "[Social] Reply API")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/{snapId}/replys")
    @Operation(summary = "댓글 등록요창", description = "댓글을 등록할 snap의 Id와 댓글내용을 보내주세요.")
    @Parameters({
            @Parameter(name = "content", description = "등록할 댓글내용", required = true, example = "안녕하세요"),
            @Parameter(name = "snapId", description = "댓글등록할 snap의 Id(Long타입으로 입력)", required = true, example = "1"),
    })
    public ResponseEntity<CommonResponseDto> addParentReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam @NotBlank(message = "댓글내용을 입력해주세요.") String content,
            @PathVariable final Long snapId){

        replyService.addParentReply(userDetails.getUsername(), snapId, content);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto("댓글등록이 성공했습니다.",null));
    }

    @PostMapping("/child-replys")
    @Operation(summary = "대댓글 등록요창", description = "대댓글을 등록할 부모댓글의 Id와 태그할 유저의 loginId,댓글내용을 입력해주세요<br>태그할 유저가 없다면 tagLoginId는 보내지 않아도 됩니다.")
    public ResponseEntity<CommonResponseDto> addChildReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestBody @Valid AddChildReplyReqDto addChildReplyReqDto){

        replyService.addChildReply(userDetails.getUsername(), addChildReplyReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto("대댓글등록이 성공했습니다.",null));
    }

    @GetMapping("/replys/parent/{snapId}/{pageNum}")
    @Operation(summary = "댓글 조회요청", description = "댓글조회할 snapId와 페이지번호를 입력해주세요<br>댓글을 20개씩 반환합니다.")
    @Parameters({
            @Parameter(name = "pageNum", description = "페이지번호", required = true, example = "1"),
            @Parameter(name = "snapId", description = "조회할 snapId", required = true, example = "1"),
    })
    public ResponseEntity<CommonResponseDto> readParentReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long pageNum,
            @PathVariable final Long snapId){

        List<FindParentReplyResDto> result = replyService.readParentReply(userDetails.getUsername(),snapId,pageNum);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto("댓글조회 성공했습니다.",result));
    }

    @GetMapping("/replys/child/{parentReplyId}/{pageNum}")
    @Operation(summary = "대댓글 조회요청", description = "대댓글조회할 부모댓글의 Id와 페이지번호를 입력해주세요<br>대댓글을 20개씩 반환합니다.")
    @Parameters({
            @Parameter(name = "pageNum", description = "페이지번호", required = true, example = "1"),
            @Parameter(name = "parentReplyId", description = "대댓글을 조회할 부모댓글의Id", required = true, example = "1"),
    })
    public ResponseEntity<CommonResponseDto> readChildReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long pageNum,
            @PathVariable final Long parentReplyId){

        List<FindChildReplyResDto> result = replyService.readChildReply(userDetails.getUsername(),parentReplyId,pageNum);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto("대댓글조회 성공했습니다.",result));
    }

    @PatchMapping("/replys/parent/{parentReplyId}")
    @Operation(summary = "댓글 수정요청", description = "댓글 ID와 수정할 댓글내용을 입력해주세요")
    @Parameters({
            @Parameter(name = "parentReplyId", description = "댓글ID", required = true, example = "1"),
            @Parameter(name = "newContent", description = "수정할 댓글내용", required = true, example = "수정된 댓글"),
    })
    public ResponseEntity<CommonResponseDto> updateParentReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long parentReplyId,
            @RequestParam final String newContent){

        replyService.updateParentReply(userDetails.getUsername(),parentReplyId,newContent);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("댓글 수정을 완료했습니다",null));
    }

    @PatchMapping("/replys/child/{childReplyId}")
    @Operation(summary = "대댓글 수정요청", description = "대댓글 ID와 수정할 댓글내용을 입력해주세요")
    @Parameters({
            @Parameter(name = "childReplyId", description = "eo댓글ID", required = true, example = "1"),
            @Parameter(name = "newContent", description = "수정할 대댓글내용", required = true, example = "수정된 대댓글"),
    })
    public ResponseEntity<CommonResponseDto> updateChildReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long childReplyId,
            @RequestParam final String newContent){

        replyService.updateChildReply(userDetails.getUsername(),childReplyId,newContent);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("대댓글 수정을 완료했습니다",null));
    }

    @DeleteMapping("/replys/parent/{parentReplyId}")
    @Operation(summary = "댓글 삭제요청", description = "삭제할 댓글 ID를 입력해주세요")
    @Parameters({
            @Parameter(name = "parentReplyId", description = "댓글ID", required = true, example = "1")
    })
    public ResponseEntity<CommonResponseDto> deleteParentReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long parentReplyId){

        replyService.deleteParentReply(userDetails.getUsername(),parentReplyId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("댓글 삭제를 완료했습니다",null));
    }

    @DeleteMapping("/replys/child/{childReplyId}")
    @Operation(summary = "대댓글 삭제요청", description = "삭제할 대댓글 ID를 입력해주세요")
    @Parameters({
            @Parameter(name = "childReplyId", description = "대댓글ID", required = true, example = "1")
    })
    public ResponseEntity<CommonResponseDto> deleteChildReply(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long childReplyId){

        replyService.deleteChildReply(userDetails.getUsername(),childReplyId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("대댓글 삭제를 완료했습니다",null));
    }
}
