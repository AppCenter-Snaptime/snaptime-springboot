package me.snaptime.user.data.dto.response.userprofile;

import lombok.Builder;

@Builder
public record ProfileCntResDto(
        Long snapCnt,
        Long followerCnt,
        Long followingCnt
){}
