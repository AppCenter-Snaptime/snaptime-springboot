package me.snaptime.snap.data.repository;

import me.snaptime.snap.data.domain.Album;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Album findByName(String name);
    List<Album> findAlbumsByUser(User user);
}
