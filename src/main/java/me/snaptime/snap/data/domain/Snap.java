package me.snaptime.snap.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;
import me.snaptime.user.data.domain.User;

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
    protected Snap(Long id, String oneLineJournal, Album album, User user,
                   String fileName, String filePath, String fileType, boolean isPrivate) {
        this.id = id;
        this.oneLineJournal = oneLineJournal;
        this.album = album;
        this.user = user;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.isPrivate = isPrivate;
    }

    public void updateIsPrivate(boolean state) {
        this.isPrivate = state;
    }

    public void associateAlbum(Album album) {
        this.album = album;
    }

    public void updateOneLineJournal(String oneLineJournal) {
        this.oneLineJournal = oneLineJournal;
    }


}
