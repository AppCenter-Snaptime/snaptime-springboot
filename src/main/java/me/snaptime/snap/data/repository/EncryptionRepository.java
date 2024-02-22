package me.snaptime.snap.data.repository;

import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncryptionRepository extends JpaRepository<Encryption, Long> {
    Encryption findByUser(User user);
}
