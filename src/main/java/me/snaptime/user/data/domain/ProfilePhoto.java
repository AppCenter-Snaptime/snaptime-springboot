package me.snaptime.user.data.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor
@Table(name="profilePhoto")
public class ProfilePhoto extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profilePhotoId")
    private Long Id;

    @Column(name = "profile_Photo_Path",nullable = false)
    private String profilePhotoPath;

    @Column(name = "profile_Photo_Name",nullable = false)
    private String profilePhotoName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId",nullable = false)
    private User user;

    @Builder
    protected ProfilePhoto(Long Id, String profilePhotoPath, String profilePhotoName,User user)
    {
        this.Id= Id;
        this.profilePhotoPath = profilePhotoPath;
        this.profilePhotoName = profilePhotoName;
        this.user = user;
    }


}
