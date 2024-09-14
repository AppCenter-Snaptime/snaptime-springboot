package me.snaptime.profile.dto.res;

import lombok.Builder;

@Builder
public record ProfileTagSnapResDto(

        Long taggedSnapId,
        String snapOwnEmail,
        String taggedSnapUrl

){}
