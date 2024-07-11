package me.snaptime.user.dto.res;


import lombok.Builder;
import me.snaptime.user.domain.User;

@Builder
public record UserResDto(
    Long id,
    String name,
    String loginId,
    String email,
    String birthDay
){
    public static UserResDto toDto(User user){
        return UserResDto.builder()
                .id(user.getId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .birthDay(user.getBirthDay())
                .build();
    }

}
