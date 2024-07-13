package me.snaptime.profilePhoto.service;

import me.snaptime.profilePhoto.dto.res.ProfilePhotoResDto;
import org.springframework.web.multipart.MultipartFile;

public interface ProfilePhotoService {
    public byte[] downloadPhotoFromFileSystem(Long profilePhotoId);

    public ProfilePhotoResDto updatePhotoFromFileSystem(String loginId, MultipartFile updateFile) throws Exception;

}
