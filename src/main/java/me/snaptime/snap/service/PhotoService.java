package me.snaptime.snap.service;

import me.snaptime.snap.data.domain.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {
    Photo uploadImageToFileSystem(MultipartFile multipartFile);
    byte[] downloadImageFromFileSystem(String fileName) throws IOException;
}
