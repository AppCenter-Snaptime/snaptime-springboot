package me.snaptime.social.data.repository.reply;

import me.snaptime.social.data.domain.ParentReply;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ParentReplyRepository extends JpaRepository<ParentReply,Long> , ParentReplyPagingRepository {

}
