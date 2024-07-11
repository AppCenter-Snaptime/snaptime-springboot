package me.snaptime.reply.repository;

import me.snaptime.reply.domain.ChildReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildReplyRepository extends JpaRepository<ChildReply,Long> , ChildReplyPagingRepository {
}
