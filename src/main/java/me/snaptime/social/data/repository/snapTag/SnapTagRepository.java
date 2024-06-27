package me.snaptime.social.data.repository.snapTag;

import me.snaptime.snap.data.domain.Snap;
import me.snaptime.social.data.domain.SnapTag;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SnapTagRepository extends JpaRepository<SnapTag,Long> {

    Optional<SnapTag> findBySnapAndTagUser(Snap snap, User tagUser);

    List<SnapTag> findBySnap(Snap snap);
}
