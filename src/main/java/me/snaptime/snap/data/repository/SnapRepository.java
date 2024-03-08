package me.snaptime.snap.data.repository;

import me.snaptime.snap.data.domain.Snap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapRepository extends JpaRepository<Snap, Long>, SnapPagingRepository{
}
