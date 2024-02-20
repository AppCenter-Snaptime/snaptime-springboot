package me.snaptime.snap.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
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
    public void createSnap(CreateSnapReqDto createSnapReqDto, String userUid) {
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new EntityNotFoundException("사용자가 요청한 uid를 가진 User를 찾을 수 없었습니다."));
        try {
            Encryption encryption = encryptionRepository.findByUser(foundUser);
            if (encryption == null) {
                // 사용자 암호화 키가 없다면 키 생성
                SecretKey generatedKey = EncryptionUtil.generateAESKey();
                // 암호화키 영속화
                encryption = encryptionRepository.save(
                        Encryption.builder()
                                .encryptionKey(generatedKey)
                                .user(foundUser)
                                .build()
                );
            }
            Photo savedPhoto = photoService.uploadPhotoToFileSystem(createSnapReqDto.multipartFile(), encryption.getEncryptionKey());
            snapRepository.save(
                    Snap.builder()
                            .oneLineJournal(createSnapReqDto.oneLineJournal())
                            .photo(savedPhoto)
                            .user(foundUser)
                            .album(albumRepository.findByName(createSnapReqDto.album()))
                            .build()
            );


        } catch(Exception e)  {
            log.error(e.getMessage());
        }

    }

    @Override
    public FindSnapResDto findSnap(Long id) {
        Snap foundSnap = snapRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("id가 존재하지 않습니다."));
        return FindSnapResDto.entityToResDto(foundSnap);
    }
}
