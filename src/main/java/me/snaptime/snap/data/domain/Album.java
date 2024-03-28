package me.snaptime.snap.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;
import me.snaptime.user.data.domain.User;

import java.util.List;

@Entity
@Getter
@Table(name = "album")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Album extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "album")
    private List<Snap> snap;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    protected Album(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }
}
