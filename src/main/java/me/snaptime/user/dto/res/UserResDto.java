package me.snaptime.user.dto.res;


import lombok.Builder;
import me.snaptime.user.domain.User;

@Builder
public record UserResDto(
    Long userId,
    String name,
    String loginId,
    String email,
    String birthDay
){
    public static UserResDto toDto(User user){
        return UserResDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .birthDay(user.getBirthDay())
                .build();
    }

}
