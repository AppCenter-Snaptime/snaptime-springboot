package me.snaptime.social.data.repository.reply;

import me.snaptime.social.data.domain.ChildReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildReplyRepository extends JpaRepository<ChildReply,Long> , ChildReplyPagingRepository {
}
