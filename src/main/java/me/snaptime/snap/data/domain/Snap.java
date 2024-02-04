package me.snaptime.snap.data.domain;

import jakarta.persistence.*;
import lombok.*;
import me.snaptime.common.domain.BaseTimeEntity;

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

    /*@ManyToOne
    @JoinColumn(name = "user_id")
    private User user;*/

    @Builder
    protected Snap(Long id, String oneLineJournal, Photo photo, Album album) {
        this.Id = id;
        this.oneLineJournal = oneLineJournal;
        this.photo = photo;
        this.album = album;
    }


}
