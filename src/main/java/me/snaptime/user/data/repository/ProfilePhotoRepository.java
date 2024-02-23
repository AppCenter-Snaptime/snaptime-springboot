package me.snaptime.user.data.repository;

import me.snaptime.user.data.domain.ProfilePhoto;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto,Long>{
    Optional<ProfilePhoto> findProfilePhotoByUser(User user);

}
