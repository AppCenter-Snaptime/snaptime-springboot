package me.snaptime.profile.dto.res;

import lombok.Builder;
import me.snaptime.user.domain.User;

@Builder
public record UserProfileResDto(
        Long userId,
        String userName,
        String profileURL,
        Boolean isFollow
){
    public static UserProfileResDto toDto(User user, String profileURL , Boolean isFollow)
    {
        return UserProfileResDto.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .profileURL(profileURL)
                .isFollow(isFollow)
                .build();
    }
}
