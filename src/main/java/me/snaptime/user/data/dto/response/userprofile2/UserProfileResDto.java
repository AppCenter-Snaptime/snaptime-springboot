package me.snaptime.user.data.dto.response.userprofile2;

import lombok.Builder;
import me.snaptime.user.data.domain.User;

@Builder
public record UserProfileResDto(
        Long userId,
        String userName,
        Long profileId
){
    public static UserProfileResDto toDto(User user)
    {
        return UserProfileResDto.builder()
                .userId(user.getId())
                .userName(user.getName())
                .profileId(user.getProfilePhoto().getId())
                .build();
    }
}
