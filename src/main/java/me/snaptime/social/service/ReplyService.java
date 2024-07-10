package me.snaptime.social.service;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.component.UrlComponent;
import me.snaptime.common.component.impl.NextPageChecker;
import me.snaptime.common.component.impl.TimeAgoCalculator;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.social.data.domain.ChildReply;
import me.snaptime.social.data.domain.ParentReply;
import me.snaptime.social.data.dto.req.AddChildReplyReqDto;
import me.snaptime.social.data.dto.req.AddParentReplyReqDto;
import me.snaptime.social.data.dto.res.ChildReplyInfo;
import me.snaptime.social.data.dto.res.FindChildReplyResDto;
import me.snaptime.social.data.dto.res.FindParentReplyResDto;
import me.snaptime.social.data.dto.res.ParentReplyInfo;
import me.snaptime.social.data.repository.reply.ChildReplyRepository;
import me.snaptime.social.data.repository.reply.ParentReplyRepository;
import me.snaptime.user.data.domain.QUser;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static me.snaptime.social.data.domain.QChildReply.childReply;
import static me.snaptime.social.data.domain.QParentReply.parentReply;
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
    private final NextPageChecker nextPageChecker;
    private final TimeAgoCalculator timeAgoCalculator;

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

            return ;
        }

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

    public FindParentReplyResDto readParentReply(String loginId, Long snapId, Long pageNum){

        List<Tuple> result = parentReplyRepository.findReplyList(loginId,snapId,pageNum);
        boolean hasNextPage = nextPageChecker.hasNextPage(result,20L);
        if(hasNextPage)
            result.remove(20);

        List<ParentReplyInfo> parentReplyInfoList = result.stream().map(entity ->
        {
            String profilePhotoURL = urlComponent.makeProfileURL(entity.get(user.profilePhoto.id));
            String timeAgo = timeAgoCalculator.findTimeAgo(entity.get(parentReply.lastModifiedDate));
            return ParentReplyInfo.toDto(entity,profilePhotoURL,timeAgo);
        }).collect(Collectors.toList());

        return FindParentReplyResDto.toDto(parentReplyInfoList, hasNextPage);
    }

    public FindChildReplyResDto readChildReply(String loginId, Long parentReplyId, Long pageNum){

        QUser writerUser = new QUser("writerUser");
        List<Tuple> result = childReplyRepository.findReplyList(loginId,parentReplyId,pageNum);
        boolean hasNextPage = nextPageChecker.hasNextPage(result,20L);
        if(hasNextPage)
            result.remove(20);

        List<ChildReplyInfo> childReplyInfoList = result.stream().map(entity ->
        {
            String profilePhotoURL = urlComponent.makeProfileURL(entity.get(writerUser.profilePhoto.id));
            String timeAgo = timeAgoCalculator.findTimeAgo(entity.get(childReply.lastModifiedDate));

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