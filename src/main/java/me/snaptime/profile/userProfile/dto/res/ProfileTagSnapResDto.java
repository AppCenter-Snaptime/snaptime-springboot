package me.snaptime.profile.userProfile.dto.res;

import lombok.Builder;

@Builder
public record ProfileTagSnapResDto(

        Long taggedSnapId,
        String snapOwnLoginId,
        String taggedSnapUrl

){}
