package me.snaptime.user.dto.res;


import lombok.Builder;
import me.snaptime.user.domain.User;

@Builder
public record UserFindResDto(
    Long userId,
    String name,
    String loginId,
    String email,
    String birthDay
){
    public static UserFindResDto toDto(User user){
        return UserFindResDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .birthDay(user.getBirthDay())
                .build();
    }

}
