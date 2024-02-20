package me.snaptime.snap.service;

import me.snaptime.snap.data.domain.Photo;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;

public interface PhotoService {
    Photo uploadPhotoToFileSystem(MultipartFile multipartFile, SecretKey secretKey);
    byte[] downloadPhotoFromFileSystem(Long photoId, String secretKey);
}
