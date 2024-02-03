package me.snaptime.user.data.repository;

import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
