package me.snaptime.user.data.repository;

import me.snaptime.user.data.domain.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto,Long> {
}
