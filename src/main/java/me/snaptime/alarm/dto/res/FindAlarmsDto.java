package me.snaptime.alarm.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindAlarmsDto(

        List<AlarmInfo> notReadAlarmInfos,
        List<AlarmInfo> readAlarmInfos

) {
    public static FindAlarmsDto toDto(List<AlarmInfo> notReadAlarmInfos, List<AlarmInfo> readAlarmInfos){

        return FindAlarmsDto.builder()
                .notReadAlarmInfos(notReadAlarmInfos)
                .readAlarmInfos(readAlarmInfos)
                .build();
    }

}
