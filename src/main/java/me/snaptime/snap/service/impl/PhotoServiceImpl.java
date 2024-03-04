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
import org.springframework.web.multipart.MultipartFile;

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
    public Photo uploadPhotoToFileSystem(MultipartFile multipartFile, SecretKey secretKey) {
        String fileName = FileNameGenerator.generatorName(multipartFile.getOriginalFilename());
        String filePath = FOLDER_PATH + fileName;
        try {
            byte[] encryptFile = EncryptionUtil.encryptData(multipartFile.getInputStream().readAllBytes(), secretKey);
            Files.write(Paths.get(filePath), encryptFile);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.ENCRYPTION_ERROR);
        }
        return photoRepository.save(
                Photo.builder()
                        .fileName(fileName)
                        .fileType(multipartFile.getContentType())
                        .filePath(filePath)
                        .build()
        );
    }

    @Override
    public Photo uploadPhotoToFileSystem(MultipartFile multipartFile) {
        String fileName = FileNameGenerator.generatorName(multipartFile.getOriginalFilename());
        String filePath = FOLDER_PATH + fileName;
        try {
            Files.write(Paths.get(filePath), multipartFile.getInputStream().readAllBytes());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_WRITE_ERROR);
        }
        return photoRepository.save(
                Photo.builder()
                        .fileName(fileName)
                        .fileType(multipartFile.getContentType())
                        .filePath(filePath)
                        .build()
        );
    }

    @Override
    public byte[] downloadPhotoFromFileSystem(Long id, String secretKey) {
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
    public void encryptionPhoto(Long id, SecretKey secretKey) {
        // 1. 사진을 찾는다
        Photo foundPhoto = photoRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.PHOTO_NOT_EXIST));
        // 2. 찾은 사진의 경로를 가져온다
        String filePath = foundPhoto.getFilePath();
        try {
            // 3. 찾은 사진의 경로를 파일 시스템에서 가져온다
            byte[] foundFile = Files.readAllBytes(new File(filePath).toPath());
            // 4. 찾아진 사진 파일 데이터를 암호화 한다.
            byte[] EncryptionData = EncryptionUtil.encryptData(foundFile, secretKey);
            // 5. 암호화한 데이터를 파일 시스템에 덮어씌운다.
            Files.write(Paths.get(filePath), EncryptionData);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_WRITE_ERROR);
        }
    }

    @Override
    public void decryptionPhoto(Long id, SecretKey secretKey) {
        Photo foundPhoto = photoRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.PHOTO_NOT_EXIST));
        String filePath = foundPhoto.getFilePath();
        try {
            byte[] foundFile = Files.readAllBytes(new File(filePath).toPath());
            byte[] EncryptionData = EncryptionUtil.decryptData(foundFile, secretKey);
            Files.write(Paths.get(filePath), EncryptionData);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_WRITE_ERROR);
        }
    }
}
