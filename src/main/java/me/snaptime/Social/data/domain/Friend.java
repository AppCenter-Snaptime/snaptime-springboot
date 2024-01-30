package me.snaptime.Social.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

    @Id
    @Column(name = "friend_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendId;

//    @ManyToOne
//    @JoinColumn(name = "to_user_id")
//    private User toUser;
//
//    @ManyToOne
//    @JoinColumn(name = "from_user_id")
//    private User fromUser;

//    @Builder
//    public Friend(User toUser, User fromUser){
//        this.toUser=toUser;
//        this.fromUser=fromUser;
//    }
}
