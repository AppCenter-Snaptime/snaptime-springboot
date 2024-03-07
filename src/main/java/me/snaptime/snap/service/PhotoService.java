package me.snaptime.snap.service;

import me.snaptime.snap.data.domain.Photo;
import javax.crypto.SecretKey;

public interface PhotoService {
    byte[] downloadPhotoFromFileSystem(Long id, SecretKey secretKey);
    void deletePhoto(Long id);
    byte[] getPhotoByte(Long id);
    void updateFileSystemPhoto(Long id, byte[] fileBytes);
    Photo writePhotoToFileSystem(String fileName, String contentType, byte[] fileBytes);
}
