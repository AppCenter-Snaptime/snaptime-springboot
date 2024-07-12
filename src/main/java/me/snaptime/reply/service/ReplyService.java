package me.snaptime.reply.service;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.reply.domain.ChildReply;
import me.snaptime.reply.domain.ParentReply;
import me.snaptime.reply.dto.req.AddChildReplyReqDto;
import me.snaptime.reply.dto.req.AddParentReplyReqDto;
import me.snaptime.reply.dto.res.ChildReplyInfo;
import me.snaptime.reply.dto.res.FindChildReplyResDto;
import me.snaptime.reply.dto.res.FindParentReplyResDto;
import me.snaptime.reply.dto.res.ParentReplyInfo;
import me.snaptime.reply.repository.ChildReplyRepository;
import me.snaptime.reply.repository.ParentReplyRepository;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.QUser;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.NextPageChecker;
import me.snaptime.util.TimeAgoCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static me.snaptime.reply.domain.QChildReply.childReply;
import static me.snaptime.reply.domain.QParentReply.parentReply;
import static me.snaptime.user.domain.QUser.user;


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
    public void addParentReply(String loginId, AddParentReplyReqDto addParentReplyReqDto){
        User user = findUserByLoginId(loginId);
        Snap snap = snapRepository.findById(addParentReplyReqDto.snapId())
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        parentReplyRepository.save(
                ParentReply.builder()
                        .user(user)
                        .snap(snap)
                        .content(addParentReplyReqDto.content())
                        .build()
        );
    }

    @Transactional
    public void addChildReply(String loginId, AddChildReplyReqDto addChildReplyReqDto){
        User user = findUserByLoginId(loginId);

        ParentReply parentReply = parentReplyRepository.findById(addChildReplyReqDto.parentReplyId())
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        // 태그유저가 없는 댓글 등록이면
        if( addChildReplyReqDto.tagLoginId() == "" || addChildReplyReqDto.tagLoginId() == null){
            childReplyRepository.save(
                    ChildReply.builder()
                            .parentReply(parentReply)
                            .user(user)
                            .content(addChildReplyReqDto.content())
                            .build()
            );
        }
        // 태그유저가 있는 댓글등록이면
        else{
            User tagUser = userRepository.findByLoginId(addChildReplyReqDto.tagLoginId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

            childReplyRepository.save(
                    ChildReply.builder()
                            .parentReply(parentReply)
                            .user(user)
                            .tagUser(tagUser)
                            .content(addChildReplyReqDto.content())
                            .build()
            );
        }
    }

    public FindParentReplyResDto readParentReply(String loginId, Long snapId, Long pageNum){

        List<Tuple> result = parentReplyRepository.findReplyList(loginId,snapId,pageNum);
        boolean hasNextPage = NextPageChecker.hasNextPage(result,20L);

        List<ParentReplyInfo> parentReplyInfoList = result.stream().map(entity ->
        {
            String profilePhotoURL = urlComponent.makeProfileURL(entity.get(user.profilePhoto.id));
            String timeAgo = TimeAgoCalculator.findTimeAgo(entity.get(parentReply.lastModifiedDate));
            return ParentReplyInfo.toDto(entity,profilePhotoURL,timeAgo);
        }).collect(Collectors.toList());

        return FindParentReplyResDto.toDto(parentReplyInfoList, hasNextPage);
    }

    public FindChildReplyResDto readChildReply(String loginId, Long parentReplyId, Long pageNum){

        QUser writerUser = new QUser("writerUser");
        List<Tuple> result = childReplyRepository.findReplyList(loginId,parentReplyId,pageNum);
        boolean hasNextPage = NextPageChecker.hasNextPage(result,20L);

        List<ChildReplyInfo> childReplyInfoList = result.stream().map(entity ->
        {
            String profilePhotoURL = urlComponent.makeProfileURL(entity.get(writerUser.profilePhoto.id));
            String timeAgo = TimeAgoCalculator.findTimeAgo(entity.get(childReply.lastModifiedDate));

            return ChildReplyInfo.toDto(entity,profilePhotoURL,timeAgo);
        }).collect(Collectors.toList());

        return FindChildReplyResDto.toDto(childReplyInfoList, hasNextPage);
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