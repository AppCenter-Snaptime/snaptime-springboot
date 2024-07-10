package me.snaptime.profile.profilePhoto.repository;

import me.snaptime.profile.profilePhoto.domain.ProfilePhoto;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto,Long>{
    Optional<ProfilePhoto> findProfilePhotoByUser(User user);

}
