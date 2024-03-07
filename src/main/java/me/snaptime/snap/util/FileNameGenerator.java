package me.snaptime.snap.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class FileNameGenerator {

    static String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmssSSS"));

    public static String generatorName(String fileName) {
        Random rd = new Random();
        return currentTime + "_" + rd.nextInt() + "_"+ fileName;
    }
}
