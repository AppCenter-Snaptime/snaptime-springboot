package me.snaptime.user.repository;

import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, UserPagingRepository{
    Optional<User> findByLoginId(String loginId);
}
