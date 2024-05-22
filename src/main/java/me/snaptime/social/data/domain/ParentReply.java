package me.snaptime.social.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.user.data.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParentReply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parentReplyId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "snap_id",nullable = false)
    private Snap snap;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToMany(mappedBy = "parentReply",cascade = CascadeType.REMOVE)
    private List<ChildReply> childReplyList;

    @Builder
    protected ParentReply(String content, Snap snap, User user){
        this.content=content;
        this.snap=snap;
        this.user=user;
    }

    public void updateReply(String content){
        this.content=content;
    }

}
