package me.snaptime.snap.data.repository;

import me.snaptime.snap.data.domain.Snap;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapRepository extends JpaRepository<Snap, Long>, SnapPagingRepository{
    Long countByUser(User user);
}
