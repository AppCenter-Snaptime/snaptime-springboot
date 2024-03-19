package me.snaptime.user.data.repository;

import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>,UserCustomRepository{
    Optional<User> findByLoginId(String loginId);
    Optional<User> findUserByName(String name);
}
