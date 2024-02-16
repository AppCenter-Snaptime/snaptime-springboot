package me.snaptime.snap.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.repository.PhotoRepository;
import me.snaptime.snap.service.PhotoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository;

    @Value("${fileSystemPath}")
    private String FOLDER_PATH;

    @Override
    public Photo uploadImageToFileSystem(MultipartFile multipartFile) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmssSSS"));
        String fileName = currentTime + multipartFile.getOriginalFilename();
        String filePath = FOLDER_PATH + fileName;
        try {
            multipartFile.transferTo(new File(filePath));
        } catch (IOException e) {
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
    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Photo foundPhoto = photoRepository.findByFileName(fileName);
        String filePath = foundPhoto.getFilePath();
        return Files.readAllBytes(new File(filePath).toPath());
    }
}
