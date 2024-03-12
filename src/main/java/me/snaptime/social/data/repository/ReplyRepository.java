package me.snaptime.social.data.repository;

import me.snaptime.social.data.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReplyRepository extends JpaRepository<Reply,Long> {

}
