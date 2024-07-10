package me.snaptime.profile.userProfile.dto.res;

import lombok.Builder;
import me.snaptime.user.domain.User;

@Builder
public record UserProfileResDto(
        Long userId,
        String userName,
        String profileURL
){
    public static UserProfileResDto toDto(User user, String profileURL)
    {
        return UserProfileResDto.builder()
                .userId(user.getId())
                .userName(user.getName())
                .profileURL(profileURL)
                .build();
    }
}
