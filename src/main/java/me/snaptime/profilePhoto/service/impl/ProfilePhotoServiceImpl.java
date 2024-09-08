package me.snaptime.profilePhoto.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.profilePhoto.domain.ProfilePhoto;
import me.snaptime.profilePhoto.dto.res.ProfilePhotoResDto;
import me.snaptime.profilePhoto.repository.ProfilePhotoRepository;
import me.snaptime.profilePhoto.service.ProfilePhotoService;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.FileNameGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfilePhotoServiceImpl implements ProfilePhotoService {
    private final UserRepository userRepository;
    private final ProfilePhotoRepository profilePhotoRepository;

    @Value("${fileSystemPath}")
    private String FOLDER_PATH;

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadPhotoFromFileSystem(Long profilePhotoId){
        ProfilePhoto profilePhoto = profilePhotoRepository.findById(profilePhotoId).orElseThrow(()->new CustomException(ExceptionCode.PROFILE_PHOTO_NOT_FOUND));
        String filePath = profilePhoto.getProfilePhotoPath();

        //jar파일에서 resource 폴더 경로가 달라지는 경우를 위한 로직
        //jar파일이 실행되면, 로컬(ide)에서의 resource 경로와, jar파일에서의 resource 경로가 달라진다.
        //JAR 파일 내부에 있는 리소스는 파일 시스템 경로가 아니라 클래스패스 내의 경로로 표현
        if(filePath.contains("/test_resource/default.png"))
        {
            //절대적인 경로로 리소스에 접근해야 합니다. 이 방법은 클래스로더를 통해 리소스에 접근하며,
            //클래스로더는 클래스패스 상에 있는 리소스에 접근할 수있다.
            try(InputStream inputStream = getClass().getResourceAsStream(filePath)){
                if(inputStream == null){
                    throw new IOException("Resource not found: " + filePath);
                }
                return inputStream.readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try{
                log.info("filePath : {}",filePath);
                Path path = new File(filePath).toPath();
                log.info("path: {}", path);
                return Files.readAllBytes(path);
            }catch (IOException e){
                log.error(e.getMessage());
                throw new CustomException(ExceptionCode.FILE_NOT_EXIST);
            }
        }
    }


    //트랜잭션 어노테이션을 사용하면 upload -> delete -> update, 프로필을 삭제를 해도 수정에 성공함.
    @Override
    @Transactional
    public ProfilePhotoResDto updatePhotoFromFileSystem(String loginId, MultipartFile updateFile) throws Exception{
        User updateUser = userRepository.findByLoginId(loginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));
        ProfilePhoto profilePhoto = profilePhotoRepository.findById(updateUser.getProfilePhoto().getProfilePhotoId()).orElseThrow(()-> new CustomException(ExceptionCode.PROFILE_PHOTO_NOT_FOUND));

        String updateFileName = FileNameGenerator.generatorName(updateFile.getOriginalFilename());
        String updateFilePath = FOLDER_PATH + updateFileName;

        try{
            if(!profilePhoto.getProfilePhotoName().equals("test_resource/default.png"))
            {
                Path path = Paths.get(profilePhoto.getProfilePhotoPath());
                Files.deleteIfExists(path);
            }
            updateFile.transferTo(new File(updateFilePath));
        }catch (IOException e){
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_NOT_EXIST);
        }

        profilePhoto.updateProfile(updateFileName,updateFilePath);

        return ProfilePhotoResDto.toDto(profilePhotoRepository.save(profilePhoto));
    }
}
