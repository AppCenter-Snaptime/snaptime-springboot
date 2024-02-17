package me.snaptime.snap.service;

import me.snaptime.snap.data.domain.Photo;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
    Photo uploadPhotoToFileSystem(MultipartFile multipartFile);
    byte[] downloadPhotoFromFileSystem(Long photoId);
}
