package me.snaptime.social.service;

import lombok.RequiredArgsConstructor;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.social.data.repository.ChildrenReplyRepository;
import me.snaptime.social.data.repository.ParentReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ParentReplyRepository parentReplyRepository;
    private final ChildrenReplyRepository childrenReplyRepository;
    private final SnapRepository snapRepository;

    @Transactional
    public void addReply(Long loginId, Long snapId){

    }

    @Transactional
    public void readReply(Long replyId, Long loginId, String content){

    }
}