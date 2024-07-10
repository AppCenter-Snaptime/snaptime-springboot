package me.snaptime.reply.repository;

import me.snaptime.reply.domain.ParentReply;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ParentReplyRepository extends JpaRepository<ParentReply,Long> , ParentReplyPagingRepository {

}
