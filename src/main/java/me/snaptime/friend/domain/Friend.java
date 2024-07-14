package me.snaptime.friend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.user.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User sender;

    @Builder
    protected Friend(User receiver, User sender){
        this.receiver = receiver;
        this.sender = sender;
    }

}
