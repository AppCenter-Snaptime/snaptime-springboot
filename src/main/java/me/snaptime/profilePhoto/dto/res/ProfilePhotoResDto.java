package me.snaptime.profilePhoto.dto.res;

import lombok.Builder;
import me.snaptime.profilePhoto.domain.ProfilePhoto;

@Builder
public record ProfilePhotoResDto(
        Long profilePhotoId,
        String profilePhotoName,
        String profilePhotoPath
){
    public static ProfilePhotoResDto toDto(ProfilePhoto profilePhoto){
        return ProfilePhotoResDto.builder()
                .profilePhotoId(profilePhoto.getProfilePhotoId())
                .profilePhotoName(profilePhoto.getProfilePhotoName())
                .profilePhotoPath(profilePhoto.getProfilePhotoPath())
                .build();
    }
}
