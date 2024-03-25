package me.snaptime.social.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.social.data.dto.req.AddChildReplyReqDto;
import me.snaptime.social.service.ReplyService;
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

    @PostMapping("/{snapId}/replys")
    public ResponseEntity<CommonResponseDto> addParentReply(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String content,
            @PathVariable final Long snapId){

        replyService.addParentReply(userDetails.getUsername(), snapId, content);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto("댓글등록이 성공했습니다.",null));
    }

    @PostMapping("/child-replys")
    public ResponseEntity<CommonResponseDto> addChildReply(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AddChildReplyReqDto addChildReplyReqDto){

        replyService.addChildReply(userDetails.getUsername(), addChildReplyReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto("대댓글등록이 성공했습니다.",null));
    }
}
