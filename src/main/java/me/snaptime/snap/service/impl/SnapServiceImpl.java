package me.snaptime.snap.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.req.ModifySnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.data.repository.EncryptionRepository;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.snap.service.PhotoService;
import me.snaptime.snap.service.SnapService;
import me.snaptime.snap.util.EncryptionUtil;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapServiceImpl implements SnapService {
    private final PhotoService photoService;
    private final SnapRepository snapRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final EncryptionRepository encryptionRepository;

    @Override
    public void createSnap(CreateSnapReqDto createSnapReqDto, String userUid, boolean isPrivate) {
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new EntityNotFoundException("사용자가 요청한 uid를 가진 User를 찾을 수 없었습니다."));
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
        Snap foundSnap = snapRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("id가 존재하지 않습니다."));
        return FindSnapResDto.entityToResDto(foundSnap);
    }

    @Override
    public void modifySnap(ModifySnapReqDto modifySnapReqDto, String userUid, boolean isPrivate) {
    }

    @Override
    public void changeVisibility(Long snapId, String userUid, boolean isPrivate) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new EntityNotFoundException("사용자가 요청한 id를 가진 Snap을 찾을 수 없었습니다."));
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new EntityNotFoundException("사용자가 요청한 uid를 가진 사용자를 찾을 수 없었습니다."));
        if (foundSnap.isPrivate() == isPrivate) {
            throw new RuntimeException("이미 설정되어 있습니다.");
        }
        try {
            if(isPrivate) {
                SecretKey secretKey = EncryptionUtil.generateAESKey();
                photoService.encryptionPhoto(foundSnap.getId(), secretKey);
                foundSnap.updateIsPrivate(true);
                encryptionRepository.save(Encryption.builder()
                                .encryptionKey(secretKey)
                                .user(foundUser).build());
            } else {
                Encryption foundEncryption = encryptionRepository.findByUser(foundUser);
                photoService.decryptionPhoto(foundSnap.getId(), foundEncryption.getEncryptionKey());
                encryptionRepository.delete(foundEncryption);
                foundSnap.updateIsPrivate(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteSnap(Long id, String Uid) {

    }

    private Photo persistPhoto(User user, MultipartFile multipartFile, boolean isPrivate) {
        if (isPrivate) {
            try {
                Encryption encryption = encryptionRepository.findByUser(user);
                if (encryption == null) {
                    SecretKey generatedKey = EncryptionUtil.generateAESKey();
                    encryption = encryptionRepository.save(
                            Encryption.builder()
                                    .encryptionKey(generatedKey)
                                    .user(user)
                                    .build()
                    );
                }
               return photoService.uploadPhotoToFileSystem(multipartFile, encryption.getEncryptionKey());
            } catch(Exception e)  {
                log.error(e.getMessage());
            }
        }
        return photoService.uploadPhotoToFileSystem(multipartFile);
    }
}
