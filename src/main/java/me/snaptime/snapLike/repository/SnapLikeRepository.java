package me.snaptime.snapLike.repository;

import me.snaptime.snap.domain.Snap;
import me.snaptime.snapLike.domain.SnapLike;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SnapLikeRepository extends JpaRepository<SnapLike,Long> {

    Optional<SnapLike> findBySnapAndUser(Snap snap, User user);

    boolean existsBySnapAndUser(Snap snap, User user);

    Long countBySnap(Snap snap);
}
