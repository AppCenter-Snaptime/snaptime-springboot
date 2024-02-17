package me.snaptime.user.data.dto.response;


import lombok.Builder;
import me.snaptime.user.data.domain.User;

@Builder
public record UserResponseDto (
    Long id,
    String name,
    String loginId,
    String password,
    String email,
    String birthDay
){
    public static UserResponseDto toDto(User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .email(user.getEmail())
                .birthDay(user.getBirthDay())
                .build();
    }

}
