package me.snaptime.user.repository;

import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, UserPagingRepository{
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByRoles(String role);
}
