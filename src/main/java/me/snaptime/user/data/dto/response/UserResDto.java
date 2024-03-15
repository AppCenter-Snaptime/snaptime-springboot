package me.snaptime.user.data.dto.response;


import lombok.Builder;
import me.snaptime.user.data.domain.User;

@Builder
public record UserResDto(
    Long id,
    String name,
    String loginId,
    String password,
    String email,
    String birthDay
){
    public static UserResDto toDto(User user){
        return UserResDto.builder()
                .id(user.getId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .email(user.getEmail())
                .birthDay(user.getBirthDay())
                .build();
    }

}
