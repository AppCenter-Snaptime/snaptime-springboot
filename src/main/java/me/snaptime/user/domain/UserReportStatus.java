package me.snaptime.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserReportStatus {

    NORMAL("일반 유저"),
    FLAGGED("신고 받은 유저"),
    BAN("이용 제한 유저"),
    ;

    private final String description;
}
