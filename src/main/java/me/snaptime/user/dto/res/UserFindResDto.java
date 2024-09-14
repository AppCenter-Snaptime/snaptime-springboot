package me.snaptime.user.dto.res;


import lombok.Builder;
import me.snaptime.user.domain.User;

@Builder
public record UserFindResDto(
    Long userId,
    String name,
    String email,
    String nickName
){
    public static UserFindResDto toDto(User user){
        return UserFindResDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .build();
    }

}
