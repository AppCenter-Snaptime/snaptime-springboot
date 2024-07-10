package me.snaptime.album.repository;

import me.snaptime.album.domain.Album;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Album findByName(String name);
    List<Album> findAlbumsByUser(User user);
    Album findAlbumsByName(String album_name);
}
