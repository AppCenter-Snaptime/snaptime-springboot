package me.snaptime.snapTag.repository;

import me.snaptime.snap.domain.Snap;
import me.snaptime.snapTag.domain.SnapTag;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SnapTagRepository extends JpaRepository<SnapTag,Long> {

    Optional<SnapTag> findBySnapAndTagUser(Snap snap, User tagUser);

    boolean existsBySnapAndTagUser(Snap snap, User tagUser);

    List<SnapTag> findBySnap(Snap snap);
}
