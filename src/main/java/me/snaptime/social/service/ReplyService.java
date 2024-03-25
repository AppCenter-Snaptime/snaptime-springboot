package me.snaptime.social.service;

import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.social.data.domain.ChildReply;
import me.snaptime.social.data.domain.ParentReply;
import me.snaptime.social.data.dto.req.AddChildReplyReqDto;
import me.snaptime.social.data.repository.ChildReplyRepository;
import me.snaptime.social.data.repository.ParentReplyRepository;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ParentReplyRepository parentReplyRepository;
    private final ChildReplyRepository childReplyRepository;
    private final UserRepository userRepository;
    private final SnapRepository snapRepository;

    @Transactional
    public void addParentReply(String loginId, Long snapId, String content){
        User user = findUserByLoginId(loginId);
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        parentReplyRepository.save(
                ParentReply.builder()
                        .user(user)
                        .snap(snap)
                        .content(content)
                        .build()
        );
    }

    @Transactional
    public void addChildReply(String loginId, AddChildReplyReqDto addChildReplyReqDto){
        User user = findUserByLoginId(loginId);
        User tagUser = findUserByLoginId(addChildReplyReqDto.tagLoginId());

        ParentReply parentReply = parentReplyRepository.findById(addChildReplyReqDto.parentReplyId())
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        childReplyRepository.save(
                ChildReply.builder()
                        .parentReply(parentReply)
                        .tagUser(tagUser)
                        .user(user)
                        .content(addChildReplyReqDto.content())
                        .build()
        );
    }

    @Transactional
    public void readReply(Long replyId, Long loginId, String content){

    }

    private User findUserByLoginId(String loginId){
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }
}