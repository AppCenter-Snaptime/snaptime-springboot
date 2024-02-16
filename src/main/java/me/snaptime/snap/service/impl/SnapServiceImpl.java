package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.snap.data.domain.Album;
import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.snap.service.PhotoService;
import me.snaptime.snap.service.SnapService;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnapServiceImpl implements SnapService {
    private final PhotoService photoService;
    private final SnapRepository snapRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    @Override
    public void createSnap(CreateSnapReqDto createSnapReqDto, String userUid) {
        // 이미지를 먼저 영속화 시킨다
       Photo savedPhoto = photoService.uploadImageToFileSystem(createSnapReqDto.multipartFile());
       // 앨범정보가 없다면 null
        Album foundAlbum = null;
        if (createSnapReqDto.album() != null) {
            foundAlbum = albumRepository.findByName(createSnapReqDto.album());
        }
       snapRepository.save(
               Snap.builder()
                       .oneLineJournal(createSnapReqDto.oneLineJournal())
                       .photo(savedPhoto)
                       .user(userRepository.findByLonginId(userUid))
                       .album(foundAlbum)
                       .build()
       );
    }
}
