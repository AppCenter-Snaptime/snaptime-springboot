package me.snaptime.alarm.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record AlarmFindAllResDto(

        List<AlarmInfoResDto> notReadAlarmInfoResDtos,
        List<AlarmInfoResDto> readAlarmInfoResDtos

) {
    public static AlarmFindAllResDto toDto(List<AlarmInfoResDto> notReadAlarmInfoResDtos, List<AlarmInfoResDto> readAlarmInfoResDtos){

        return AlarmFindAllResDto.builder()
                .notReadAlarmInfoResDtos(notReadAlarmInfoResDtos)
                .readAlarmInfoResDtos(readAlarmInfoResDtos)
                .build();
    }

}
