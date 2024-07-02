package me.snaptime.user.data.dto.response.userprofile;

import lombok.Builder;

@Builder
public record ProfileTagSnapResDto(

        Long taggedSnapId,
        String snapOwnLoginId,
        String taggedSnapUrl

){}
