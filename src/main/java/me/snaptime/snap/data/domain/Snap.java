package me.snaptime.snap.data.domain;

import jakarta.persistence.*;
import lombok.*;
import me.snaptime.Social.data.domain.Reply;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;
import me.snaptime.user.data.domain.User;

import java.util.List;

@Entity
@Getter
@Table(name = "snap")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Snap extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String oneLineJournal;

    @OneToOne
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;


//    @OneToMany(mappedBy = "snap")
//    private List<Reply> replyList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    protected Snap(Long id, String oneLineJournal, Photo photo, Album album, List<Reply> replyList, User user) {
        this.Id = id;
        this.oneLineJournal = oneLineJournal;
        this.photo = photo;
        this.album = album;
//        this.replyList = replyList;
        this.user = user;
    }


}
