package me.snaptime.profilePhoto.service;

import me.snaptime.profilePhoto.dto.res.ProfilePhotoResDto;
import org.springframework.web.multipart.MultipartFile;

public interface ProfilePhotoService {
    byte[] downloadPhotoFromFileSystem(Long profilePhotoId);

    ProfilePhotoResDto updatePhotoFromFileSystem(String userEmail, MultipartFile updateFile) throws Exception;

}
