package me.snaptime.profile.userProfile.dto.res;

import lombok.Builder;

@Builder
public record ProfileCntResDto(
        Long snapCnt,
        Long followerCnt,
        Long followingCnt
){}
