package me.snaptime.snapTag.dto.res;

import lombok.Builder;
import me.snaptime.snapTag.domain.SnapTag;

@Builder
public record TagUserFindResDto(

        String tagUserLoginId,
        String tagUserName

) {
    public static TagUserFindResDto toDto(SnapTag snapTag){
        return TagUserFindResDto.builder()
                .tagUserLoginId(snapTag.getTagUser().getLoginId())
                .tagUserName(snapTag.getTagUser().getName())
                .build();
    }
}

