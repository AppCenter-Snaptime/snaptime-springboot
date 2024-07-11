package me.snaptime.profilePhoto.dto.res;

import lombok.Builder;
import me.snaptime.profilePhoto.domain.ProfilePhoto;

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
