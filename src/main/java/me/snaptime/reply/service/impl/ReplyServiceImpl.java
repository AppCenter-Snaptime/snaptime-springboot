package me.snaptime.reply.service.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.service.AlarmAddService;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.reply.domain.ChildReply;
import me.snaptime.reply.domain.ParentReply;
import me.snaptime.reply.dto.req.ChildReplyAddReqDto;
import me.snaptime.reply.dto.req.ParentReplyAddReqDto;
import me.snaptime.reply.dto.res.ChildReplyInfoResDto;
import me.snaptime.reply.dto.res.ChildReplyPagingResDto;
import me.snaptime.reply.dto.res.ParentReplyInfoResDto;
import me.snaptime.reply.dto.res.ParentReplyPagingResDto;
import me.snaptime.reply.repository.ChildReplyRepository;
import me.snaptime.reply.repository.ParentReplyRepository;
import me.snaptime.reply.service.ReplyService;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.NextPageChecker;
import me.snaptime.util.TimeAgoCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    private final AlarmAddService alarmAddService;

    @Override
    @Transactional
    public void addParentReply(String reqLoginId, ParentReplyAddReqDto parentReplyAddReqDto){
        User reqUser = findUserByLoginId(reqLoginId);
        Snap snap = snapRepository.findById(parentReplyAddReqDto.snapId())
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        parentReplyRepository.save(
                ParentReply.builder()
                        .user(reqUser)
                        .snap(snap)
                        .content(parentReplyAddReqDto.replyMessage())
                        .build()
        );

        alarmAddService.createReplyAlarm(reqUser, snap.getUser(), snap, parentReplyAddReqDto.replyMessage());
    }

    @Transactional
    public void addChildReply(String reqLoginId, ChildReplyAddReqDto childReplyAddReqDto){
        User reqUser = findUserByLoginId(reqLoginId);

        ParentReply parentReply = parentReplyRepository.findById(childReplyAddReqDto.parentReplyId())
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_FOUND));

        // 태그유저가 없는 댓글 등록이면
        if( childReplyAddReqDto.tagLoginId().isBlank()){
            childReplyRepository.save(
                    ChildReply.builder()
                            .parentReply(parentReply)
                            .user(reqUser)
                            .content(childReplyAddReqDto.replyMessage())
                            .build()
            );
        }
        // 태그유저가 있는 댓글등록이면
        else{
            User tagUser = userRepository.findByLoginId(childReplyAddReqDto.tagLoginId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

            childReplyRepository.save(
                    ChildReply.builder()
                            .parentReply(parentReply)
                            .user(reqUser)
                            .tagUser(tagUser)
                            .content(childReplyAddReqDto.replyMessage())
                            .build()
            );
        }
    }

    public ParentReplyPagingResDto findParentReplyPage(Long snapId, Long pageNum){

        List<Tuple> tuples = parentReplyRepository.findReplyPage(snapId,pageNum);
        boolean hasNextPage = NextPageChecker.hasNextPage(tuples,20L);

        List<ParentReplyInfoResDto> parentReplyInfoResDtos = tuples.stream().map(tuple ->
        {
            String profilePhotoURL = urlComponent.makeProfileURL(tuple.get(user.profilePhoto.profilePhotoId));
            String timeAgo = TimeAgoCalculator.findTimeAgo(tuple.get(parentReply.lastModifiedDate));
            return ParentReplyInfoResDto.toDto(tuple,profilePhotoURL,timeAgo);
        }).collect(Collectors.toList());

        return ParentReplyPagingResDto.toDto(parentReplyInfoResDtos, hasNextPage);
    }

    public ChildReplyPagingResDto findChildReplyPage(Long parentReplyId, Long pageNum){

        List<ChildReply> childReplies = childReplyRepository.findReplyPage(parentReplyId,pageNum);
        boolean hasNextPage = NextPageChecker.hasNextPageByChildReplies(childReplies,20L);

        List<ChildReplyInfoResDto> childReplyInfoResDtos = childReplies.stream().map(childReply ->
        {
            String profilePhotoURL = urlComponent.makeProfileURL( childReply.getUser().getProfilePhoto().getProfilePhotoId());
            String timeAgo = TimeAgoCalculator.findTimeAgo( childReply.getCreatedDate());

            return ChildReplyInfoResDto.toDto( childReply,profilePhotoURL,timeAgo);
        }).collect(Collectors.toList());

        return ChildReplyPagingResDto.toDto(childReplyInfoResDtos, hasNextPage);
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