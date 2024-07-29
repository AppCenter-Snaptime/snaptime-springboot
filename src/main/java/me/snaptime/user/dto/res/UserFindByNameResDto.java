package me.snaptime.user.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.user.domain.QUser.user;

@Builder
public record UserFindByNameResDto (

        String foundLoginId,
        String profilePhotoURL,
        String foundUserName
){
    public static UserFindByNameResDto toDto(Tuple tuple, String profilePhotoURL){
        return UserFindByNameResDto.builder()
                .foundLoginId(tuple.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .foundUserName(tuple.get(user.name))
                .build();
    }
}
