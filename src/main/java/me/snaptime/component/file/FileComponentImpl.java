package me.snaptime.component.file;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.dto.file.WritePhotoToFileSystemResult;
import me.snaptime.util.FileNameGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileComponentImpl implements FileComponent {

    @Value("${fileSystemPath}")
    private String FOLDER_PATH;

    @Override
    public byte[] downloadPhotoFromFileSystem(String fileName) {
        String filePath = FOLDER_PATH + fileName;
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_READ_ERROR);
        }
    }

    @Override
    public void deletePhoto(String fileName) {
        String filePath = FOLDER_PATH + fileName;
        try {
            Path path = Paths.get(filePath);
            Files.delete(path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_DELETE_ERROR);
        }
    }

    @Override
    public byte[] getPhotoByte(String filePath) {
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_READ_ERROR);
        }
    }

    @Override
    public void updateFileSystemPhoto(String filePath, byte[] fileBytes) {
        try {
            Files.write(Paths.get(filePath), fileBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_WRITE_ERROR);
        }
    }

    @Override
    public WritePhotoToFileSystemResult writePhotoToFileSystem(String fileName, String contentType, byte[] fileBytes) {
        String generatedName = FileNameGenerator.generatorName(fileName);
        String filePath = FOLDER_PATH + generatedName;
        try {
            Files.write(Paths.get(filePath), fileBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_WRITE_ERROR);
        }
        return new WritePhotoToFileSystemResult(
                filePath, generatedName
        );
    }
}
