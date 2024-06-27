package me.snaptime.social.data.dto.res;

import lombok.Builder;
import me.snaptime.social.data.domain.SnapTag;

@Builder
public record FindTagUserResDto(

        String loginId,
        String userName

) {
    public static FindTagUserResDto toDto(SnapTag snapTag){
        return FindTagUserResDto.builder()
                .loginId(snapTag.getTagUser().getLoginId())
                .userName(snapTag.getTagUser().getName())
                .build();
    }
}

