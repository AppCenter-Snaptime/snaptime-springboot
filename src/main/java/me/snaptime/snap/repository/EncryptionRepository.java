package me.snaptime.snap.repository;

import me.snaptime.snap.domain.Encryption;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncryptionRepository extends JpaRepository<Encryption, Long> {
    Encryption findByUser(User user);
}
