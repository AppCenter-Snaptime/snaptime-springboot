package me.snaptime.user.data.dto.response.userprofile;

import lombok.Builder;
import me.snaptime.user.data.domain.User;

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
