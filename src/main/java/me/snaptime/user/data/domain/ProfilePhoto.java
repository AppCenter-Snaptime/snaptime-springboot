package me.snaptime.user.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profilePhoto")
public class ProfilePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profilePhoto_id")
    private Long profilePhotoId;

    @Column(name = "profilePhoto_name",nullable = false)
    private String profilePhotoName;

    @Column(name = "profilePhoto_path",nullable = false)
    private String profilePhotoPath;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    protected ProfilePhoto(Long profilePhotoId,String profilePhotoName,String profilePhotoPath)
    {
        this.profilePhotoId = profilePhotoId;
        this.profilePhotoName = profilePhotoName;
        this.profilePhotoPath = profilePhotoPath;
    }
}
