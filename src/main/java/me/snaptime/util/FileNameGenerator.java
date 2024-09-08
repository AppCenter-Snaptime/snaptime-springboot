package me.snaptime.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class FileNameGenerator {


    public static String generatorName(String fileName) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmssSSS"));
        Random rd = new Random();
        int nonNegativeInt = Math.abs(rd.nextInt()); // 음수를 방지하기 위해 절대값을 취합니다.
        return currentTime + "_" + nonNegativeInt + "_" + fileName;
    }
}
