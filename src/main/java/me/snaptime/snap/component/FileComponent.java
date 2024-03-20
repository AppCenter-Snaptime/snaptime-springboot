package me.snaptime.snap.component;

import me.snaptime.snap.data.dto.file.WritePhotoToFileSystemResult;

public interface FileComponent {
    byte[] downloadPhotoFromFileSystem(String filePath);
    void deletePhoto(String fileName);
    byte[] getPhotoByte(String filePath);
    void updateFileSystemPhoto(String filePath, byte[] fileBytes);
    WritePhotoToFileSystemResult writePhotoToFileSystem(String fileName, String contentType, byte[] fileBytes);
}
