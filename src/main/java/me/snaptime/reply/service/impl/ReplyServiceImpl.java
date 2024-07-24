package me.snaptime.reply.service.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.service.CreateAlarmService;
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
import me.snaptime.reply.service.ReplyService;
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
public class ReplyServiceImpl implements ReplyService {

    private final ParentReplyRepository parentReplyRepository;
    private final ChildReplyRepository childReplyRepository;
    private final UserRepository userRepository;
    private final SnapRepository snapRepository;
    private final UrlComponent urlComponent;
    private final CreateAlarmService createAlarmService;

    @Override
    @Transactional
    public void addParentReply(String reqLoginId, AddParentReplyReqDto addParentReplyReqDto){
        User reqUser = findUserByLoginId(reqLoginId);
        Snap snap = snapRepository.findById(addParentReplyReqDto.snapId())
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        parentReplyRepository.save(
                ParentReply.builder()
                        .user(reqUser)
                        .snap(snap)
                        .content(addParentReplyReqDto.replyMessage())
                        .build()
        );

        createAlarmService.createReplyAlarm(reqUser, snap.getUser(), snap, addParentReplyReqDto.replyMessage());
    }

    @Transactional
    public void addChildReply(String reqLoginId, AddChildReplyReqDto addChildReplyReqDto){
        User reqUser = findUserByLoginId(reqLoginId);

        ParentReply parentReply = parentReplyRepository.findById(addChildReplyReqDto.parentReplyId())
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        // 태그유저가 없는 댓글 등록이면
        if( addChildReplyReqDto.tagLoginId() == "" || addChildReplyReqDto.tagLoginId() == null){
            childReplyRepository.save(
                    ChildReply.builder()
                            .parentReply(parentReply)
                            .user(reqUser)
                            .content(addChildReplyReqDto.replyMessage())
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
                            .user(reqUser)
                            .tagUser(tagUser)
                            .content(addChildReplyReqDto.replyMessage())
                            .build()
            );
        }
    }

    public FindParentReplyResDto findParentReplyPage(Long snapId, Long pageNum){

        List<Tuple> tuples = parentReplyRepository.findReplyList(snapId,pageNum);
        boolean hasNextPage = NextPageChecker.hasNextPage(tuples,20L);

        List<ParentReplyInfo> parentReplyInfoList = tuples.stream().map( tuple ->
        {
            String profilePhotoURL = urlComponent.makeProfileURL(tuple.get(user.profilePhoto.profilePhotoId));
            String timeAgo = TimeAgoCalculator.findTimeAgo(tuple.get(parentReply.lastModifiedDate));
            return ParentReplyInfo.toDto(tuple,profilePhotoURL,timeAgo);
        }).collect(Collectors.toList());

        return FindParentReplyResDto.toDto(parentReplyInfoList, hasNextPage);
    }

    public FindChildReplyResDto findChildReplyPage(Long parentReplyId, Long pageNum){

        QUser writerUser = new QUser("writerUser");
        List<Tuple> tuples = childReplyRepository.findReplyList(parentReplyId,pageNum);
        boolean hasNextPage = NextPageChecker.hasNextPage(tuples,20L);

        List<ChildReplyInfo> childReplyInfoList = tuples.stream().map( tuple ->
        {
            String profilePhotoURL = urlComponent.makeProfileURL( tuple.get(writerUser.profilePhoto.profilePhotoId));
            String timeAgo = TimeAgoCalculator.findTimeAgo( tuple.get(childReply.lastModifiedDate));

            return ChildReplyInfo.toDto( tuple,profilePhotoURL,timeAgo);
        }).collect(Collectors.toList());

        return FindChildReplyResDto.toDto(childReplyInfoList, hasNextPage);
    }

    @Transactional
    public void updateParentReply(String reqLoginId ,Long parentReplyId, String newContent){
        ParentReply parentReply = parentReplyRepository.findById(parentReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        isMyReply(reqLoginId,parentReply.getUser().getLoginId());
        parentReply.updateReply(newContent);
        parentReplyRepository.save(parentReply);
    }

    @Transactional
    public void updateChildReply(String reqLoginId, Long childReplyId, String newContent){
        ChildReply childReply = childReplyRepository.findById(childReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        isMyReply(reqLoginId,childReply.getUser().getLoginId());
        childReply.updateReply(newContent);
        childReplyRepository.save(childReply);
    }

    @Transactional
    public void deleteParentReply(String reqLoginId, Long parentReplyId){
        ParentReply parentReply = parentReplyRepository.findById(parentReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        isMyReply(reqLoginId,parentReply.getUser().getLoginId());
        parentReplyRepository.delete(parentReply);
    }

    @Transactional
    public void deleteChildReply(String reqLoginId, Long childReplyId){
        ChildReply childReply = childReplyRepository.findById(childReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        isMyReply(reqLoginId,childReply.getUser().getLoginId());
        childReplyRepository.delete(childReply);
    }

    private User findUserByLoginId(String loginId){
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
    }

    private void isMyReply(String reqLoginId, String targetLoginId){

        if(!targetLoginId.equals(reqLoginId))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_REPLY);
    }
}