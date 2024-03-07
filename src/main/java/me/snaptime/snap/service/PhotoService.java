package me.snaptime.snap.service;

import me.snaptime.snap.data.dto.file.WritePhotoToFileSystemResult;

import javax.crypto.SecretKey;

public interface PhotoService {
    byte[] downloadPhotoFromFileSystem(String filePath, SecretKey secretKey);
    byte[] downloadPhotoFromFileSystem(String filePath);
    void deletePhoto(Long id);
    byte[] getPhotoByte(String filePath);
    void updateFileSystemPhoto(String filePath, byte[] fileBytes);
    WritePhotoToFileSystemResult writePhotoToFileSystem(String fileName, String contentType, byte[] fileBytes);
}
