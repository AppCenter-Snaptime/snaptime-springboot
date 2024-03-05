package me.snaptime.snap.service;

import me.snaptime.snap.data.domain.Photo;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;

public interface PhotoService {
    Photo uploadPhotoToFileSystem(MultipartFile multipartFile, SecretKey secretKey);
    Photo uploadPhotoToFileSystem(MultipartFile multipartFile);
    byte[] downloadPhotoFromFileSystem(Long id, SecretKey secretKey);
    void deletePhoto(Long id);
    void encryptPhoto(Long id, SecretKey secretKey);
    void decryptPhoto(Long id, SecretKey secretKey);
}
