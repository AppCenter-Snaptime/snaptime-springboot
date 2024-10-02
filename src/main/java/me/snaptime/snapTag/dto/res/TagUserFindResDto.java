package me.snaptime.snapTag.dto.res;

import lombok.Builder;
import me.snaptime.snapTag.domain.SnapTag;

@Builder
public record TagUserFindResDto(

        String tagUserEmail,
        String tagUserName,
        String tagName,
        String tagUserProfileUrl,
        boolean isFollow

) {
    public static TagUserFindResDto toDto(SnapTag snapTag, String tagUserProfileUrl, boolean isFollow){
        return TagUserFindResDto.builder()
                .tagUserEmail(snapTag.getTagUser().getEmail())
                .tagUserName(snapTag.getTagUser().getName())
                .tagName(snapTag.getTagUser().getName())
                .tagUserProfileUrl(tagUserProfileUrl)
                .isFollow(isFollow)
                .build();
    }
}

