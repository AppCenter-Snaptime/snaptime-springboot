package me.snaptime.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.snap.util.FileNameGenerator;
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
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfilePhotoService {

    private final UserRepository userRepository;
    private final ProfilePhotoRepository profilePhotoRepository;

    @Value("${fileSystemPath}")
    private String FOLDER_PATH;

    @Transactional
    public ProfilePhotoResponseDto uploadPhotoToFileSystem(Long userId, MultipartFile uploadFile) throws Exception{
        User createUser = userRepository.findById(userId).orElseThrow(()-> new NoSuchElementException("프로필 사진을 업로드 할 유저가 존재하지 않습니다."));
        String fileName = ProfilePhotoNameGenerator.generatorProfilePhotoName(uploadFile.getOriginalFilename());
        String filePath = FOLDER_PATH + fileName;

        try{
            uploadFile.transferTo(new File(filePath));
        }catch (IOException e){
            log.error(e.getMessage());
        }

        return ProfilePhotoResponseDto.toDto(profilePhotoRepository.save(
                ProfilePhoto.builder()
                        .profilePhotoName(fileName)
                        .profilePhotoPath(filePath)
                        .user(createUser)
                        .build()));
    }


    public byte[] downloadPhotoFromFileSystem(Long id){
        ProfilePhoto profilePhoto = profilePhotoRepository.findById(id).orElseThrow(()->new NoSuchElementException("해당하는 id의 프로필 사진을 찾을 수 없습니다."));
        String filePath = profilePhoto.getProfilePhotoPath();

        try{
            return Files.readAllBytes(new File(filePath).toPath());
        }catch (IOException e){
            byte[] emptyByte = {};
            log.error(e.getMessage());
            return emptyByte;
        }
    }


    public void deletePhotoFromFileSystem(Long userId) throws Exception{
        User deleteUser = userRepository.findById(userId).orElseThrow(()-> new NoSuchElementException("삭제 할 유저가 존재하지 않습니다."));
        ProfilePhoto profilePhoto = profilePhotoRepository.findProfilePhotoByUser(deleteUser).orElseThrow(()-> new NoSuchElementException("해당하는 프로필 사진이 존재하지 않습니다."));
        profilePhotoRepository.deleteById(profilePhoto.getId());

        String photoPath = profilePhoto.getProfilePhotoPath();
        if(!photoPath.isEmpty()){
            try{
                Path path = Paths.get(photoPath);
                Files.deleteIfExists(path);
            }catch (Exception e){
                e.printStackTrace();
                throw new Exception("프로필 사진 파일 삭제를 실패하였습니다.");
            }
        }
    }

    @Transactional
    public ProfilePhotoResponseDto updatePhotoFromFileSystem(Long userId, MultipartFile updateFile) throws Exception{
        User updateUser = userRepository.findById(userId).orElseThrow(()-> new NoSuchElementException("수정 할 유저가 존재하지 않습니다."));
        ProfilePhoto profilePhoto = profilePhotoRepository.findProfilePhotoByUser(updateUser).orElseThrow(()-> new NoSuchElementException("수정 할 프로필 사진이 없습니다."));

        String updateFileName = ProfilePhotoNameGenerator.generatorProfilePhotoName(updateFile.getOriginalFilename());
        String updateFilePath = FOLDER_PATH + updateFileName;

        try{
            Path path = Paths.get(profilePhoto.getProfilePhotoPath());
            Files.deleteIfExists(path);
            updateFile.transferTo(new File(updateFilePath));
        }catch (IOException e){
            log.error(e.getMessage());
        }

        profilePhoto.updateProfile(updateFileName,updateFilePath);

        return ProfilePhotoResponseDto.toDto(profilePhotoRepository.save(profilePhoto));
    }

}
