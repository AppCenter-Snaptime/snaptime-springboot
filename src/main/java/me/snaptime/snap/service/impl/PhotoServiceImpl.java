package me.snaptime.snap.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.repository.PhotoRepository;
import me.snaptime.snap.service.PhotoService;
import me.snaptime.snap.util.EncryptionUtil;
import me.snaptime.snap.util.FileNameGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository;

    @Value("${fileSystemPath}")
    private String FOLDER_PATH;

    @Override
    public byte[] downloadPhotoFromFileSystem(Long id, SecretKey secretKey) {
        Photo foundPhoto = photoRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.PHOTO_NOT_EXIST));
        String filePath = foundPhoto.getFilePath();
        try {
            byte[] foundFile = Files.readAllBytes(new File(filePath).toPath());
            return EncryptionUtil.decryptData(foundFile, secretKey);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_READ_ERROR);
        }
    }

    @Override
    public void deletePhoto(Long id) {
        Photo foundPhoto = photoRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.PHOTO_NOT_EXIST));
        String filePath = foundPhoto.getFilePath();
        try {
            Path path = Paths.get(filePath);
            Files.delete(path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_DELETE_ERROR);
        }
        photoRepository.delete(foundPhoto);
    }

    @Override
    public byte[] getPhotoByte(Long id) {
        Photo foundPhoto = photoRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.PHOTO_NOT_EXIST));
        String filePath = foundPhoto.getFilePath();
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_READ_ERROR);
        }
    }

@Override
    public void updateFileSystemPhoto(Long id, byte[] fileBytes) {
        Photo foundPhoto = photoRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.PHOTO_NOT_EXIST));
        String filePath = foundPhoto.getFilePath();
        try {
            Files.write(Paths.get(filePath), fileBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_WRITE_ERROR);
        }
    }

    @Override
    public Photo writePhotoToFileSystem(String fileName, String contentType, byte[] fileBytes) {
        String generatedName = FileNameGenerator.generatorName(fileName);
        String filePath = FOLDER_PATH + generatedName;
        try {
            Files.write(Paths.get(filePath), fileBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_WRITE_ERROR);
        }
        return photoRepository.save(
                Photo.builder()
                        .fileName(fileName)
                        .fileType(contentType)
                        .filePath(filePath)
                        .build()
        );
    }
}
