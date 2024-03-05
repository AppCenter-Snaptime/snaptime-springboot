package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.req.ModifySnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.data.repository.EncryptionRepository;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.snap.service.EncryptionService;
import me.snaptime.snap.service.PhotoService;
import me.snaptime.snap.service.SnapService;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapServiceImpl implements SnapService {
    private final PhotoService photoService;
    private final SnapRepository snapRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final EncryptionService encryptionService;
    private final EncryptionRepository encryptionRepository;

    @Override
    public void createSnap(CreateSnapReqDto createSnapReqDto, String userUid, boolean isPrivate) {
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Photo savedPhoto = persistPhoto(foundUser, createSnapReqDto.multipartFile(), isPrivate);
        snapRepository.save(
                Snap.builder()
                        .oneLineJournal(createSnapReqDto.oneLineJournal())
                        .photo(savedPhoto)
                        .user(foundUser)
                        .album(albumRepository.findByName(createSnapReqDto.album()))
                        .isPrivate(isPrivate)
                        .build()
        );

    }

    @Override
    public FindSnapResDto findSnap(Long id) {
        Snap foundSnap = snapRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        return FindSnapResDto.entityToResDto(foundSnap);
    }

    @Override
    public void modifySnap(ModifySnapReqDto modifySnapReqDto, String userUid, boolean isPrivate) {
    }

    @Override
    public void changeVisibility(Long snapId, String userUid, boolean isPrivate) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        if (foundSnap.isPrivate() == isPrivate) {
            throw new RuntimeException("이미 설정되어 있습니다.");
        }
        try {
            if(isPrivate) {
                Encryption encryption = encryptionService.setEncryption(foundUser);
                photoService.encryptPhoto(foundSnap.getId(), encryption.getSecretKey());
                foundSnap.updateIsPrivate(true);
            } else {
                Encryption encryption = encryptionService.getEncryption(foundUser);
                photoService.decryptPhoto(foundSnap.getId(), encryption.getSecretKey());
                encryptionRepository.delete(encryption);
                foundSnap.updateIsPrivate(false);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.ENCRYPTION_ERROR);
        }
        snapRepository.save(foundSnap);

    }

    @Override
    public void deleteSnap(Long id, String Uid) {

    }

    private Photo persistPhoto(User user, MultipartFile multipartFile, boolean isPrivate) {
        if (isPrivate) {
            try {
                Encryption encryption = encryptionService.setEncryption(user);
               return photoService.uploadPhotoToFileSystem(multipartFile, encryption.getSecretKey());
            } catch(Exception e)  {
                log.error(e.getMessage());
                throw new CustomException(ExceptionCode.ENCRYPTION_ERROR);
            }
        }
        return photoService.uploadPhotoToFileSystem(multipartFile);
    }
}
