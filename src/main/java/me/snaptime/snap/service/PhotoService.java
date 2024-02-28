package me.snaptime.snap.service;

import me.snaptime.snap.data.domain.Photo;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;

public interface PhotoService {
    Photo uploadPhotoToFileSystem(MultipartFile multipartFile, SecretKey secretKey);
    Photo uploadPhotoToFileSystem(MultipartFile multipartFile);
    byte[] downloadPhotoFromFileSystem(Long id, String secretKey);
    void deletePhoto(Long id);
    void encryptionPhoto(Long id, SecretKey secretKey);
    void decryptionPhoto(Long id, SecretKey secretKey);
}
