package me.snaptime.snap.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
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
       Photo savedPhoto = photoService.uploadPhotoToFileSystem(createSnapReqDto.multipartFile());
       snapRepository.save(
               Snap.builder()
                       .oneLineJournal(createSnapReqDto.oneLineJournal())
                       .photo(savedPhoto)
                       .user(userRepository.findByLonginId(userUid))
                       .album(albumRepository.findByName(createSnapReqDto.album()))
                       .build()
       );
    }

    @Override
    public FindSnapResDto findSnap(Long id) {
        Snap foundSnap = snapRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("id가 존재하지 않습니다."));
        return FindSnapResDto.entityToResDto(foundSnap);
    }
}
