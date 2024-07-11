package me.snaptime.profile.dto.res;

import lombok.Builder;

@Builder
public record ProfileCntResDto(
        Long snapCnt,
        Long followerCnt,
        Long followingCnt
){}
