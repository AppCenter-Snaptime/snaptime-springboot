package me.snaptime.snapTag.dto.res;

import lombok.Builder;
import me.snaptime.snapTag.domain.SnapTag;

@Builder
public record FindTagUserResDto(

        String tagUserLoginId,
        String tagUserName

) {
    public static FindTagUserResDto toDto(SnapTag snapTag){
        return FindTagUserResDto.builder()
                .tagUserLoginId(snapTag.getTagUser().getLoginId())
                .tagUserName(snapTag.getTagUser().getName())
                .build();
    }
}

