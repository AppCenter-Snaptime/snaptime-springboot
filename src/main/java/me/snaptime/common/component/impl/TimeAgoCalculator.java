package me.snaptime.common.component.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class TimeAgoCalculator {

    public String findTimeAgo(LocalDateTime calculatedTime){

        // 현재 서울 시간 구하기
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime nowSeoulLocalDateTime = currentTime.toLocalDateTime();

        // 시간 차이 계산
        long years = ChronoUnit.YEARS.between(calculatedTime, nowSeoulLocalDateTime);
        long months = ChronoUnit.MONTHS.between(calculatedTime, nowSeoulLocalDateTime);
        long days = ChronoUnit.DAYS.between(calculatedTime, nowSeoulLocalDateTime);
        long hours = ChronoUnit.HOURS.between(calculatedTime, nowSeoulLocalDateTime);
        long minutes = ChronoUnit.MINUTES.between(calculatedTime, nowSeoulLocalDateTime);

        if (years > 0) {
            return years + "년 전";
        } else if (months > 0) {
            return months + "달 전";
        } else if (days > 0) {
            return days + "일 전";
        } else if (hours > 0) {
            return hours + "시간 전";
        } else if (minutes > 0) {
            return minutes + "분 전";
        } else {
            return "방금 전";
        }
    }

}
