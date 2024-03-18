package me.snaptime.user.data.dto.response;

import lombok.Builder;
import me.snaptime.user.data.domain.ProfilePhoto;

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
