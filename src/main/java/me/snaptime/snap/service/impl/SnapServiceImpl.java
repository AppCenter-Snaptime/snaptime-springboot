package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.album.domain.Album;
import me.snaptime.album.repository.AlbumRepository;
import me.snaptime.album.service.AlbumService;
import me.snaptime.component.encryption.EncryptionComponent;
import me.snaptime.component.file.FileComponent;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.domain.Encryption;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.dto.file.WritePhotoToFileSystemResult;
import me.snaptime.snap.dto.req.CreateSnapReqDto;
import me.snaptime.snap.dto.req.ModifySnapReqDto;
import me.snaptime.snap.dto.res.FindSnapResDto;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.snap.service.SnapService;
import me.snaptime.snapTag.service.SnapTagService;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.EncryptionUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.IOException;
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
    public Long createSnap(CreateSnapReqDto createSnapReqDto, String userUid, boolean isPrivate, List<String> tagUserLoginIds, Long album_id) {
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

        // 사용자가 앨범 선택을 하고 요청을 보낼 경우
        if (album_id != null) {
            // 사용자가 보낸 앨범 id가 유효한지 확인한다.
            if (albumService.isAlbumExistById(album_id)) {
                // 사용자가 만든 앨범인지 확인 한다.
                albumService.isUserHavePermission(foundUser, album_id);
                // 위 구문을 실행하는데 문제가 없다면 연관관계를 맺어준다.
                makeRelationSnapAndAlbum(savedSnap, album_id);
            }
        } else {
            // 사용자가 앨범 선택을 하지 않고 요청을 보낼 경우
            // non-classification 앨범에 스냅을 추가함
            processSnapForNonClassification(savedSnap, foundUser);
        }

        // tagUserLoginIds가 파라미터로 주어졌을 경우 태그에 추가
        if (tagUserLoginIds != null) {
            snapTagService.addTagUser(tagUserLoginIds, savedSnap);
        }

        return savedSnap.getId();
    }

    @Override
    public FindSnapResDto findSnap(Long id, String uId) {
        Snap foundSnap = snapRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        if(foundSnap.isPrivate()) {
            User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
            // Snap이 비공개라면, 요청한 유저와 스냅의 ID가 일치하는지 확인한다.
            if (!Objects.equals(foundUser.getId(), foundSnap.getUser().getId())) {
                throw new CustomException(ExceptionCode.SNAP_IS_PRIVATE);
            }
        }
        String snapPhotoUrl = urlComponent.makePhotoURL(foundSnap.getFileName(), foundSnap.isPrivate());
        String profilePhotoUrl = urlComponent.makeProfileURL(foundSnap.getUser().getProfilePhoto().getId());
        return FindSnapResDto.entityToResDto(foundSnap, snapPhotoUrl, profilePhotoUrl);
    }

    @Override
    public Long modifySnap(Long snapId, ModifySnapReqDto modifySnapReqDto, String userUid, List<String> tagUserLoginIds, boolean isPrivate) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        // 수정하려는 유저와 수정되려는 스냅의 저자가 일치하는지 확인한다.
        if (!foundSnap.getUser().getId().equals(foundUser.getId())) {
            throw new CustomException(ExceptionCode.SNAP_USER_IS_NOT_THE_SAME);
        }

        // 이미지 수정
        if (null != modifySnapReqDto.multipartFile()) {
            try {
                byte[] foundPhotoByte = modifySnapReqDto.multipartFile().getInputStream().readAllBytes();
                if (isPrivate) {
                    // 사용자가 이미지 수정까지 요구했고, 암호화까지 원한다면
                    // getEncryption을 통해 사용자의 암호화키를 가져온다. (없으면 null이다.)
                    Encryption encryption = encryptionComponent.getEncryption(foundUser);
                    // 사용자의 암호화키가 존재하는지 확인한다.
                    if (encryption != null) {
                        // 기존 암호화키가 존재할 경우 기존의 암호화키로 데이터를 암호화한 후 저장소에 쓰기 한다.
                        byte[] encryptedByte = encryptionComponent.encryptData(encryption, foundPhotoByte);
                        fileComponent.updateFileSystemPhoto(foundSnap.getFilePath(), encryptedByte);
                    } else {
                        // 암호화키가 존재하지 않을 경우 새로운 암호화키를 생성 한 뒤에 저장소에 쓰기 한다.
                        Encryption newEncryption = encryptionComponent.setEncryption(foundUser);
                        byte[] encryptedByte = encryptionComponent.encryptData(newEncryption, foundPhotoByte);
                        fileComponent.updateFileSystemPhoto(foundSnap.getFilePath(), encryptedByte);
                    }
                    // Snap의 암호화 상태를 활성으로 변경한다.
                    foundSnap.updateIsPrivate(true);
                    // 태그 목록을 삭제한다.
                    snapTagService.deleteAllTagUser(foundSnap);
                } else {
                    // 사용자가 이미지 수정을 요구하였으나, 암호화를 원하지 않는다면
                    // fileComponent를 통해 원래 경로에 사진을 저장한다.
                    fileComponent.updateFileSystemPhoto(foundSnap.getFilePath(), foundPhotoByte);
                    // Snap의 암호화 상태를 비활성화로 변경한다.
                    foundSnap.updateIsPrivate(false);

                    if (tagUserLoginIds != null) {
                        snapTagService.modifyTagUser(tagUserLoginIds, foundSnap);
                    }
                }
            } catch (IOException e) {
                throw new CustomException(ExceptionCode.SNAP_MODIFY_ERROR);
            }
        }

        Snap snap = snapRepository.save(foundSnap);
        return snap.getId();
    }

    @Override
    public void changeVisibility(Long snapId, String userUid, boolean isPrivate) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        // 설정되어 있는 값하고 똑같다면
        if (foundSnap.isPrivate() == isPrivate) {
            // 예외를 발생시킨다.
            throw new CustomException(ExceptionCode.CHANGE_SNAP_VISIBILITY_ERROR);
        }

        // 저장된 filePath 경로로 부터 파일을 가져온다.
        byte[] foundPhotoByte = fileComponent.getPhotoByte(foundSnap.getFilePath());
        if(isPrivate) {
            // public -> private (암호화)
            Encryption encryption = encryptionComponent.setEncryption(foundUser);
            byte[] encryptedByte = encryptionComponent.encryptData(encryption, foundPhotoByte);
            fileComponent.updateFileSystemPhoto(foundSnap.getFilePath(), encryptedByte);
            // 태그 목록을 삭제한다.
            snapTagService.deleteAllTagUser(foundSnap);
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
    public void deleteSnap(Long snapId, String uId) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        // 삭제를 요청한 사용자가 Snap를 만든 사용자인지 확인한다.
        if (!Objects.equals(foundSnap.getUser().getId(), foundUser.getId())) {
            // 다르다면 에러를 던진다.
            throw new CustomException(ExceptionCode.SNAP_USER_IS_NOT_THE_SAME);
        }
        String fileName = foundSnap.getFileName();
        // 저장소에 보관되어있는 사진을 삭제한다.
        fileComponent.deletePhoto(fileName);
        // DB에서 스냅을 삭제한다.
        snapRepository.delete(foundSnap);
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

    @Override
    public void relocateSnap(Long snapId, Long albumId, String uId) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        Album foundAlbum = albumRepository.findById(albumId).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        // 찾은 Snap의 소유자가 요청자와 일치하고, 새로 옮길 앨범의 소유자가 요청자와 일치한다면
        if (Objects.equals(foundSnap.getUser().getId(), foundUser.getId()) && Objects.equals(foundSnap.getAlbum().getUser().getId(), foundUser.getId())) {
            // 새로 연관관계를 맺어주고 DB에 반영한다.
            foundSnap.associateAlbum(foundAlbum);
            snapRepository.save(foundSnap);
        } else {
            throw new CustomException(ExceptionCode.ALBUM_USER_NOT_MATCH);
        }
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
        snap.associateAlbum(foundAlbum);
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
