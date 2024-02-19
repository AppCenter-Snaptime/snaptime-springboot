package me.snaptime.user.data.repository;

import me.snaptime.user.data.domain.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto,Long> ,
        QuerydslPredicateExecutor<ProfilePhoto> {
}
