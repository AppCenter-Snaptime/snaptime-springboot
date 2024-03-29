package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.component.EncryptionComponent;
import me.snaptime.snap.component.FileComponent;
import me.snaptime.common.component.UrlComponent;
import me.snaptime.snap.data.domain.Album;
import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.file.WritePhotoToFileSystemResult;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.req.ModifySnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.snap.service.SnapService;
import me.snaptime.snap.util.EncryptionUtil;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapServiceImpl implements SnapService {
    private final SnapRepository snapRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final FileComponent fileComponent;
    private final EncryptionComponent encryptionComponent;
    private final UrlComponent urlComponent;

    @Override
    public Long createSnap(CreateSnapReqDto createSnapReqDto, String userUid, boolean isPrivate) {
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        WritePhotoToFileSystemResult writePhotoToFileSystemResult = savePhotoToFileSystem(foundUser, createSnapReqDto.multipartFile(), isPrivate);
        Snap result = snapRepository.save(
                Snap.builder()
                        .oneLineJournal(createSnapReqDto.oneLineJournal())
                        .fileName(writePhotoToFileSystemResult.fileName())
                        .filePath(writePhotoToFileSystemResult.filePath())
                        .fileType(createSnapReqDto.multipartFile().getContentType())
                        .user(foundUser)
                        .isPrivate(isPrivate)
                        .build()
        );

        return result.getId();
    }

    @Override
    public FindSnapResDto findSnap(Long id) {
        Snap foundSnap = snapRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        if(foundSnap.isPrivate()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String uId = userDetails.getUsername();
            User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
            if (foundUser != foundSnap.getUser()) {
                throw new CustomException(ExceptionCode.SNAP_USER_IS_NOT_THE_SAME);
            }
        }
        String photoUrl = urlComponent.makePhotoURL(foundSnap.getFileName(), foundSnap.isPrivate());
        return FindSnapResDto.entityToResDto(foundSnap, photoUrl);
    }

    @Override
    public void modifySnap(ModifySnapReqDto modifySnapReqDto, String userUid, boolean isPrivate) {
    }

    @Override
    @Transactional
    public void makeRelationSnapAndAlbum(Long snap_id, Album album) {
        Snap foundSnap = snapRepository.findById(snap_id).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        foundSnap.updateAlbum(album);
        snapRepository.save(foundSnap);
    }

    @Override
    public void changeVisibility(Long snapId, String userUid, boolean isPrivate) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (foundSnap.isPrivate() == isPrivate) {
            throw new CustomException(ExceptionCode.CHANGE_SNAP_VISIBILITY_ERROR);
        }

        byte[] foundPhotoByte = fileComponent.getPhotoByte(foundSnap.getFilePath());
        if(isPrivate) {
            // public -> private (암호화)
            Encryption encryption = encryptionComponent.setEncryption(foundUser);
            byte[] encryptedByte = encryptionComponent.encryptData(encryption, foundPhotoByte);
            fileComponent.updateFileSystemPhoto(foundSnap.getFilePath(), encryptedByte);
        } else {
            // private -> public (복호화)
            Encryption encryption = encryptionComponent.getEncryption(foundUser);
            byte[] decryptedByte = encryptionComponent.decryptData(encryption, foundPhotoByte);
            encryptionComponent.deleteEncryption(encryption);
            fileComponent.updateFileSystemPhoto(foundSnap.getFilePath(), decryptedByte);
        }
        foundSnap.updateIsPrivate(isPrivate);

        snapRepository.save(foundSnap);

    }

    @Override
    public void deleteSnap(Long id, String Uid) {

    }

    @Override
    public byte[] downloadPhotoFromFileSystem(String fileName, String uId, boolean isEncrypted) {
        byte[] photoData = fileComponent.downloadPhotoFromFileSystem(fileName);
        if (isEncrypted) {
            try {
                SecretKey secretKey = encryptionComponent.getSecretKey(uId);
                return EncryptionUtil.decryptData(photoData, secretKey);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new CustomException(ExceptionCode.ENCRYPTION_ERROR);
            }
        }
        return photoData;
    }

    private WritePhotoToFileSystemResult savePhotoToFileSystem(User user, MultipartFile multipartFile, boolean isPrivate) {
        try {
            if (isPrivate) {
                Encryption encryption = encryptionComponent.setEncryption(user);
                byte[] encryptedData = encryptionComponent.encryptData(encryption, multipartFile.getInputStream().readAllBytes());
                return fileComponent.writePhotoToFileSystem(multipartFile.getOriginalFilename(), multipartFile.getContentType(), encryptedData);
            } else {
                return fileComponent.writePhotoToFileSystem(multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getInputStream().readAllBytes());
        }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_READ_ERROR);
        }
    }

}
