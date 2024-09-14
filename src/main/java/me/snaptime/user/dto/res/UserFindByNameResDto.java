package me.snaptime.user.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.user.domain.QUser.user;

@Builder
public record UserFindByNameResDto (

        String foundEmail,
        String profilePhotoURL,
        String foundUserName,
        String foundNickName
){
    public static UserFindByNameResDto toDto(Tuple tuple, String profilePhotoURL){
        return UserFindByNameResDto.builder()
                .foundEmail(tuple.get(user.email))
                .profilePhotoURL(profilePhotoURL)
                .foundUserName(tuple.get(user.name))
                .foundNickName(tuple.get(user.nickName))
                .build();
    }
}
