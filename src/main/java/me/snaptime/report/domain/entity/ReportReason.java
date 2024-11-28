package me.snaptime.report.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportReason {
    SPAM("스팸"),
    INAPPROPRIATE_CONTENT("선정적 내용"),
    HATE_SPEECH("혐오 발언"),
    VIOLENCE("폭력"),
    ILLEGAL_CONTENT("불법 내용"),
    HARASSMENT("괴롭힘"),
    COPYRIGHT_INFRINGEMENT("저작권 침해"),
    MISINFORMATION("거짓 정보");

    private final String description;
}
