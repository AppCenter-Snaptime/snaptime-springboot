package me.snaptime.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.user.data.domain.ProfilePhoto;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.response.ProfilePhotoResponseDto;
import me.snaptime.user.data.repository.ProfilePhotoRepository;
import me.snaptime.user.data.repository.UserRepository;
import me.snaptime.user.util.ProfilePhotoNameGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfilePhotoService {

    private final UserRepository userRepository;
    private final ProfilePhotoRepository profilePhotoRepository;

    @Value("${fileSystemPath}")
    private String FOLDER_PATH;

    @Transactional(readOnly = true)
    public byte[] downloadPhotoFromFileSystem(String loginId){
        log.info("[downloadPhoto] 유저의 로그인 아이디로 유저를 불러옵니다. loginId : {}",loginId);
        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));
        ProfilePhoto profilePhoto = profilePhotoRepository.findById(user.getProfilePhoto().getId()).orElseThrow(()-> new CustomException(ExceptionCode.PROFILE_PHOTO_NOT_FOUND));
        log.info("[downloadPhoto] 해당 유저의 프로필 사진을 불러옵니다. profileId : {}",profilePhoto.getId());
        String filePath = profilePhoto.getProfilePhotoPath();

        try{
            return Files.readAllBytes(new File(filePath).toPath());
        }catch (IOException e){
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_NOT_EXIST);
        }

    }


    //트랜잭션 어노테이션을 사용하면 upload -> delete -> update, 프로필을 삭제를 해도 수정에 성공함.
    //@Transactional
    public ProfilePhotoResponseDto updatePhotoFromFileSystem(String loginId, MultipartFile updateFile) throws Exception{
        User updateUser = userRepository.findByLoginId(loginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));
        ProfilePhoto profilePhoto = profilePhotoRepository.findById(updateUser.getProfilePhoto().getId()).orElseThrow(()-> new CustomException(ExceptionCode.PROFILE_PHOTO_NOT_FOUND));

        String updateFileName = ProfilePhotoNameGenerator.generatorProfilePhotoName(updateFile.getOriginalFilename());
        String updateFilePath = FOLDER_PATH + updateFileName;

        try{
            Path path = Paths.get(profilePhoto.getProfilePhotoPath());
            Files.deleteIfExists(path);
            updateFile.transferTo(new File(updateFilePath));
        }catch (IOException e){
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_NOT_EXIST);
        }

        profilePhoto.updateProfile(updateFileName,updateFilePath);

        return ProfilePhotoResponseDto.toDto(profilePhotoRepository.save(profilePhoto));
    }

    //    @Transactional
//    public ProfilePhotoResponseDto uploadPhotoToFileSystem(String loginId, MultipartFile uploadFile) throws Exception{
//        User createUser = userRepository.findByLoginId(loginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));
//
//        //이미 프로필 사진이 존재하는 경우
//        if(profilePhotoRepository.findProfilePhotoByUser(createUser).isPresent())
//        {
//            throw new CustomException(ExceptionCode.PROFILE_PHOTO_EXIST);
//        }
//
//        String fileName = ProfilePhotoNameGenerator.generatorProfilePhotoName(uploadFile.getOriginalFilename());
//        String filePath = FOLDER_PATH + fileName;
//
//        try{
//            uploadFile.transferTo(new File(filePath));
//        }catch (IOException e){
//            log.error(e.getMessage());
//            throw new CustomException(ExceptionCode.FILE_NOT_EXIST);
//        }
//
//        return ProfilePhotoResponseDto.toDto(profilePhotoRepository.save(
//                ProfilePhoto.builder()
//                        .profilePhotoName(fileName)
//                        .profilePhotoPath(filePath)
//                        .build()));
//    }


    //트랜잭션 어노테이션을 사용하면 upload -> delete -> delete, 삭제를 2번해도 성공메시지가 뜸
    //@Transactional
//    public void deletePhotoFromFileSystem(Long userId) throws Exception{
//        User deleteUser = userRepository.findById(userId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));
//        ProfilePhoto profilePhoto = profilePhotoRepository.findProfilePhotoByUser(deleteUser).orElseThrow(()-> new CustomException(ExceptionCode.PROFILE_PHOTO_NOT_FOUND));
//        profilePhotoRepository.deleteById(profilePhoto.getId());
//
//        String photoPath = profilePhoto.getProfilePhotoPath();
//
//        if(!photoPath.isEmpty()){
//            try{
//                Path path = Paths.get(photoPath);
//                Files.deleteIfExists(path);
//            }catch (Exception e){
//                e.printStackTrace();
//                throw new CustomException(ExceptionCode.FILE_NOT_EXIST);
//            }
//        }
//    }

}
