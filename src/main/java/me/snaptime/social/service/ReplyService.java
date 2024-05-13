package me.snaptime.social.service;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.component.UrlComponent;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.social.data.domain.ChildReply;
import me.snaptime.social.data.domain.ParentReply;
import me.snaptime.social.data.dto.req.AddChildReplyReqDto;
import me.snaptime.social.data.dto.res.FindChildReplyResDto;
import me.snaptime.social.data.dto.res.FindParentReplyResDto;
import me.snaptime.social.data.repository.reply.ChildReplyRepository;
import me.snaptime.social.data.repository.reply.ParentReplyRepository;
import me.snaptime.user.data.domain.QUser;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.snaptime.user.data.domain.QUser.user;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ParentReplyRepository parentReplyRepository;
    private final ChildReplyRepository childReplyRepository;
    private final UserRepository userRepository;
    private final SnapRepository snapRepository;
    private final UrlComponent urlComponent;

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
        User tagUser = userRepository.findByLoginId(addChildReplyReqDto.tagLoginId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

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

    public List<FindParentReplyResDto> readParentReply(String loginId, Long snapId, Long pageNum){

        List<Tuple> result = parentReplyRepository.findReplyList(loginId,snapId,pageNum);

        return result.stream().map(entity -> {
            String profilePhotoURL = urlComponent.makeProfileURL(entity.get(user.profilePhoto.id));
            return FindParentReplyResDto.toDto(entity,profilePhotoURL);
        }).collect(Collectors.toList());
    }

    public List<FindChildReplyResDto> readChildReply(String loginId, Long parentReplyId, Long pageNum){
        QUser writerUser = new QUser("writerUser");
        List<Tuple> result = childReplyRepository.findReplyList(loginId,parentReplyId,pageNum);

        return result.stream().map(entity -> {
            String profilePhotoURL = urlComponent.makeProfileURL(entity.get(writerUser.profilePhoto.id));
            return FindChildReplyResDto.toDto(entity,profilePhotoURL);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateParentReply(String loginId ,Long parentReplyId, String newContent){
        ParentReply parentReply = parentReplyRepository.findById(parentReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        if(!parentReply.getUser().getLoginId().equals(loginId))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_REPLY);

        parentReply.updateReply(newContent);
        parentReplyRepository.save(parentReply);
    }

    @Transactional
    public void updateChildReply(String loginId, Long childReplyId, String newContent){
        ChildReply childReply = childReplyRepository.findById(childReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        if(!childReply.getUser().getLoginId().equals(loginId))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_REPLY);

        childReply.updateReply(newContent);
        childReplyRepository.save(childReply);
    }

    @Transactional
    public void deleteParentReply(String loginId, Long parentReplyId){
        ParentReply parentReply = parentReplyRepository.findById(parentReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        if(!parentReply.getUser().getLoginId().equals(loginId))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_REPLY);

        parentReplyRepository.delete(parentReply);
    }

    @Transactional
    public void deleteChildReply(String loginId, Long childReplyId){
        ChildReply childReply = childReplyRepository.findById(childReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        if(!childReply.getUser().getLoginId().equals(loginId))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_REPLY);

        childReplyRepository.delete(childReply);
    }

    private User findUserByLoginId(String loginId){
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
    }
}