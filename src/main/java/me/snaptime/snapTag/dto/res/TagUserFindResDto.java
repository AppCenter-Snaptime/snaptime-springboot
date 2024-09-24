package me.snaptime.snapTag.dto.res;

import lombok.Builder;
import me.snaptime.snapTag.domain.SnapTag;

@Builder
public record TagUserFindResDto(

        String tagUserEmail,
        String tagUserName

) {
    public static TagUserFindResDto toDto(SnapTag snapTag){
        return TagUserFindResDto.builder()
                .tagUserEmail(snapTag.getTagUser().getEmail())
                .tagUserName(snapTag.getTagUser().getName())
                .build();
    }
}

