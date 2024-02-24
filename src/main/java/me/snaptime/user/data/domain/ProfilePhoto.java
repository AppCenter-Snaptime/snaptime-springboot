package me.snaptime.user.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="profile_photo")
public class ProfilePhoto extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_photo_id")
    private Long Id;

    @Column(name = "profile_photo_path",nullable = false)
    private String profilePhotoPath;

    @Column(name = "profile_photo_name",nullable = false)
    private String profilePhotoName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Builder
    protected ProfilePhoto(Long Id, String profilePhotoPath, String profilePhotoName,User user)
    {
        this.Id= Id;
        this.profilePhotoPath = profilePhotoPath;
        this.profilePhotoName = profilePhotoName;
        this.user = user;
    }

    public void updateProfile(String profilePhotoName,String profilePhotoPath)
    {
        this.profilePhotoName = profilePhotoName;
        this.profilePhotoPath = profilePhotoPath;
    }
}
