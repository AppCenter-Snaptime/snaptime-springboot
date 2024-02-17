package me.snaptime.snap.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileNameGenerator {
    static String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmssSSS"));

    public static String generatorName(String fileName) {
        return currentTime + fileName;
    }
}
