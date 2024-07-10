package me.snaptime.profile.profilePhoto.dto.res;

import lombok.Builder;
import me.snaptime.profile.profilePhoto.domain.ProfilePhoto;

@Builder
public record ProfilePhotoResDto(
        Long id,
        String profilePhotoName,
        String profilePhotoPath
){
    public static ProfilePhotoResDto toDto(ProfilePhoto profilePhoto){
        return ProfilePhotoResDto.builder()
                .id(profilePhoto.getId())
                .profilePhotoName(profilePhoto.getProfilePhotoName())
                .profilePhotoPath(profilePhoto.getProfilePhotoPath())
                .build();
    }
}
