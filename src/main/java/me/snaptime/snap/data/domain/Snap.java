package me.snaptime.snap.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;
import me.snaptime.social.data.domain.Reply;
import me.snaptime.user.data.domain.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "snap")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Snap extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oneLineJournal;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @OneToMany(mappedBy = "snap")
    private List<Reply> replyList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isPrivate;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileType;

    @Builder
    protected Snap(Long id, String oneLineJournal, Album album, List<Reply> replyList, User user,
                    String fileName, String filePath, String fileType, boolean isPrivate) {
        this.id = id;
        this.oneLineJournal = oneLineJournal;
        this.album = album;
        this.replyList = replyList;
        this.user = user;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.isPrivate = isPrivate;
    }

    public void updateIsPrivate(boolean state) {
        this.isPrivate = state;
    }


}
