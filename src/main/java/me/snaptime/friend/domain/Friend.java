package me.snaptime.friend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.friend.common.FriendStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "friend_status")
    private FriendStatus friendStatus;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @Builder
    protected Friend(FriendStatus friendStatus, User toUser, User fromUser){
        this.toUser=toUser;
        this.friendStatus=friendStatus;
        this.fromUser=fromUser;
    }

    public void updateFriendStatus(FriendStatus friendStatus){
        this.friendStatus=friendStatus;
    }
}
