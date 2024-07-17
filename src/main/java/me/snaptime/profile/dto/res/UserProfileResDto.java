package me.snaptime.profile.dto.res;

import lombok.Builder;
import me.snaptime.user.domain.User;

@Builder
public record UserProfileResDto(
        Long userId,
        String userName,
        String profileURL
//        Boolean isFollow
){
    public static UserProfileResDto toDto(User user, String profileURL /*, boolean isFollow*/)
    {
        return UserProfileResDto.builder()
                .userId(user.getId())
                .userName(user.getName())
                .profileURL(profileURL)
                //.isFollow(isFollow)
                .build();
    }
}
