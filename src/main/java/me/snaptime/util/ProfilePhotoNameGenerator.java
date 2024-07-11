package me.snaptime.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProfilePhotoNameGenerator {
    static String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmssSSS"));

    public static String generatorProfilePhotoName(String fileName) {
        return currentTime + fileName;
    }
}
