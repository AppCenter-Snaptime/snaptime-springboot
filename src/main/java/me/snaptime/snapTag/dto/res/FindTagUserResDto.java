package me.snaptime.snapTag.dto.res;

import lombok.Builder;
import me.snaptime.snapTag.domain.SnapTag;

@Builder
public record FindTagUserResDto(

        String tagUserLoginId,
        String userName

) {
    public static FindTagUserResDto toDto(SnapTag snapTag){
        return FindTagUserResDto.builder()
                .tagUserLoginId(snapTag.getTagUser().getLoginId())
                .userName(snapTag.getTagUser().getName())
                .build();
    }
}

