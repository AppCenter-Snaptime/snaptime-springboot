package me.snaptime.snap.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.user.data.domain.User;

import javax.crypto.SecretKey;

@Entity
@Getter
@Table(name = "encryption")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Encryption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private SecretKey encryptionKey;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    protected Encryption(SecretKey encryptionKey, User user) {
        this.encryptionKey = encryptionKey;
        this.user = user;
    }
}
