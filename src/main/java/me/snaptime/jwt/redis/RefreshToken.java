package me.snaptime.jwt.redis;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 86400)
public class RefreshToken {
    private String refreshToken;

    @Id
    @Indexed
    private Long id;

    public RefreshToken(Long id, String refreshToken){
        this.refreshToken = refreshToken;
        this.id = id;
    }
}
