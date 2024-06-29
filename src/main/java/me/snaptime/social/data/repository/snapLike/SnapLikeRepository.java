package me.snaptime.social.data.repository.snapLike;

import me.snaptime.snap.data.domain.Snap;
import me.snaptime.social.data.domain.SnapLike;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SnapLikeRepository extends JpaRepository<SnapLike,Long> {

    Optional<SnapLike> findBySnapAndUser(Snap snap, User user);
}
