package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.component.UrlComponent;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.component.EncryptionComponent;
import me.snaptime.snap.component.FileComponent;
import me.snaptime.snap.data.domain.Album;
import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.file.WritePhotoToFileSystemResult;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.req.ModifySnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.snap.service.AlbumService;
import me.snaptime.snap.service.SnapService;
import me.snaptime.snap.util.EncryptionUtil;
import me.snaptime.social.service.SnapTagService;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Objects;

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
    private final SnapTagService snapTagService;
    private final AlbumService albumService;

    @Override
    public Long createSnap(CreateSnapReqDto createSnapReqDto, String userUid, boolean isPrivate, List<String> tagUserLoginIds, boolean nonClassification) {
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        WritePhotoToFileSystemResult writePhotoToFileSystemResult = savePhotoToFileSystem(foundUser, createSnapReqDto.multipartFile(), isPrivate);
        Snap savedSnap = snapRepository.save(
                Snap.builder()
                        .oneLineJournal(createSnapReqDto.oneLineJournal())
                        .fileName(writePhotoToFileSystemResult.fileName())
                        .filePath(writePhotoToFileSystemResult.filePath())
                        .fileType(createSnapReqDto.multipartFile().getContentType())
                        .user(foundUser)
                        .isPrivate(isPrivate)
                        .build()
        );

        // tagUserLoginIds가 파라미터로 주어졌을 경우 태그에 추가
        if (tagUserLoginIds != null) {
            snapTagService.addTagUser(tagUserLoginIds, savedSnap);
        }

        // 사용자가 앨범을 선택하지 않고 요청을 보낼 경우
        if (nonClassification) {
            // non-classification 앨범에 스냅을 추가함
            processSnapForNonClassification(savedSnap, foundUser);
        } else {

        }

        return savedSnap.getId();
    }

    @Override
    public FindSnapResDto findSnap(Long id, String uId) {
        Snap foundSnap = snapRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        if(foundSnap.isPrivate()) {
            User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

            if (!Objects.equals(foundUser.getId(), foundSnap.getUser().getId())) {
                throw new CustomException(ExceptionCode.SNAP_USER_IS_NOT_THE_SAME);
            }
        }
        String snapPhotoUrl = urlComponent.makePhotoURL(foundSnap.getFileName(), foundSnap.isPrivate());
        String profilePhotoUrl = urlComponent.makeProfileURL(foundSnap.getUser().getProfilePhoto().getId());
        return FindSnapResDto.entityToResDto(foundSnap, snapPhotoUrl, profilePhotoUrl);
    }

    @Override
    public void modifySnap(ModifySnapReqDto modifySnapReqDto, String userUid, boolean isPrivate) {
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

    private void makeRelationSnapAndAlbum(Snap snap, Long album_id) {
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        snap.updateAlbum(foundAlbum);
        snapRepository.save(snap);
    }

    private void processSnapForNonClassification(Snap snap, User user){
        // 분류되지 않은 앨범이 사용자에게 이미 존재하는지 확인함
        if(albumService.isNonClassificationExist(user)) {
            // 존재한다면 분류되지 않은 앨범에 추가함
            Long foundNonClassificationAlbumId = albumService.findUserNonClassificationId(user);
            makeRelationSnapAndAlbum(snap, foundNonClassificationAlbumId);
        } else {
            // 존재하지 않는다면 분류되지 않은 앨범을 생성하고 앨범에 추가함
            Long createdNonClassificationAlbumId = albumService.createNonClassificationAlbum(user);
            makeRelationSnapAndAlbum(snap, createdNonClassificationAlbumId);
        }
    }

}
