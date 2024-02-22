package me.snaptime.snap.service.impl;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public byte[] downloadPhotoFromFileSystem(Long photoId, String secretKey) {
        Photo foundPhoto = photoRepository.findById(photoId).orElseThrow(() -> new EntityNotFoundException("id에 해당되는 사진을 찾을 수 없습니다."));
        String filePath = foundPhoto.getFilePath();
        try {
            byte[] foundFile = Files.readAllBytes(new File(filePath).toPath());
            return EncryptionUtil.decryptData(foundFile, secretKey);
        } catch (Exception e) {
            byte[] emptyByte = {};
            log.error(e.getMessage());
            return emptyByte;
        }
    }
}
