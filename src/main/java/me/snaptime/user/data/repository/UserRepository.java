package me.snaptime.user.data.repository;

import me.snaptime.user.data.domain.ProfilePhoto;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> ,
        QuerydslPredicateExecutor<ProfilePhoto>{
    Optional<User> findByLoginId(String loginId);
    Optional<User> findUserByName(String name);
}
