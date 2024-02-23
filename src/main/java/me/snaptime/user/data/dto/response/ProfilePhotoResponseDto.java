package me.snaptime.user.data.dto.response;

import lombok.Builder;
import me.snaptime.user.data.domain.ProfilePhoto;

@Builder
public record ProfilePhotoResponseDto(
        Long id,
        Long userId,
        String profilePhotoName,
        String profilePhotoPath
){
    public static ProfilePhotoResponseDto toDto(ProfilePhoto profilePhoto){
        return ProfilePhotoResponseDto.builder()
                .id(profilePhoto.getId())
                .userId(profilePhoto.getUser().getId())
                .profilePhotoName(profilePhoto.getProfilePhotoName())
                .profilePhotoPath(profilePhoto.getProfilePhotoPath())
                .build();
    }
}
