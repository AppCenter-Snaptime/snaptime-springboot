package me.snaptime.snap.data.repository;

import me.snaptime.snap.data.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Album findByName(String name);
}
