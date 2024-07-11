package me.snaptime.component.file;

import me.snaptime.snap.dto.file.WritePhotoToFileSystemResult;

public interface FileComponent {
    byte[] downloadPhotoFromFileSystem(String filePath);
    void deletePhoto(String fileName);
    byte[] getPhotoByte(String filePath);
    void updateFileSystemPhoto(String filePath, byte[] fileBytes);
    WritePhotoToFileSystemResult writePhotoToFileSystem(String fileName, String contentType, byte[] fileBytes);
}
