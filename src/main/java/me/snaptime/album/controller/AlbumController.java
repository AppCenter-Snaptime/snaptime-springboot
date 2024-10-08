package me.snaptime.album.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.album.dto.req.CreateAlbumReqDto;
import me.snaptime.album.dto.res.FindAlbumResDto;
import me.snaptime.album.dto.res.FindAllAlbumsResDto;
import me.snaptime.album.dto.res.GetAllAlbumListResDto;
import me.snaptime.album.service.AlbumService;
import me.snaptime.common.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "[Album] Album API")
@RequestMapping("/album")
public class AlbumController {
    private final AlbumService albumService;

    @Operation(summary = "Album 목록(썸네일 포함) 조회", description = "사용자의 Album 목록(썸네일 포함)을 조회합니다.")
    @GetMapping(path = "/albumListWithThumbnail")
    public ResponseEntity<CommonResponseDto<List<FindAllAlbumsResDto>>> findAllAlbumsByEmail(
            final @AuthenticationPrincipal UserDetails userDetails
            ) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new CommonResponseDto<>(
                                "사용자의 앨범 목록(썸네일 포함)이 정상적으로 불러와졌습니다.",
                                albumService.findAllAlbumsByEmail(userEmail)
                        )
                );
    }

    @Operation(summary = "Album 생성", description = "사용자의 Album을 생성합니다.")
    @PostMapping
    public ResponseEntity<CommonResponseDto<Void>> createAlbum(
            final @RequestBody CreateAlbumReqDto createAlbumReqDto,
            final @AuthenticationPrincipal UserDetails userDetails
            ) {
        String userEmail = userDetails.getUsername();
        albumService.createAlbum(createAlbumReqDto, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CommonResponseDto<>(
                        "사용자의 앨범을 정상적으로 생성했습니다.",
                        null
                )
        );
    }

    @Operation(summary = "Album 목록 불러오기", description = "사용자의 Album 목록을 불러옵니다.")
    @GetMapping(path = "/albumLists")
    public ResponseEntity<CommonResponseDto<List<GetAllAlbumListResDto>>> findAlbumList(
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "사용자의 앨범 목록(앨범의 이름들)을 정상적으로 가져왔습니다.",
                        albumService.getAlbumListByEmail(userEmail)
                )
        );
    }

    @Operation(summary = "한 Album의 내용을 전부 가져오기", description = "Album의 내용을 전부 가져옵니다.")
    @GetMapping("/{album_id}")
    public ResponseEntity<CommonResponseDto<FindAlbumResDto>> findAlbum(
            final @PathVariable("album_id") Long album_id,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        album_id + "번 Album의 내용을 전부 가져오는데 성공했습니다.",
                        albumService.findAlbum(userEmail, album_id)
                )
        );
    }

    @Operation(summary = "Album의 이름을 변경합니다.")
    @PatchMapping
    public ResponseEntity<CommonResponseDto<Void>> modifyAlbumName(
            final @RequestParam("album_name") String name,
            final @RequestParam("album_id") Long id
    ) {
        albumService.modifyAlbumName(id, name);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        id + "번 앨범의 이름을 수정했습니다.",
                        null
                )
        );
    }

    @Operation(summary = "Album을 삭제합니다.", description = "Album Id로 Album을 삭제합니다. 삭제된 앨범 안에 있는 Snap은 '모든 스냅' 앨범으로 이동됩니다.")
    @Parameter(name = "albumId", description = "삭제될 앨범의 ID를 입력해주세요")
    @DeleteMapping
    ResponseEntity<CommonResponseDto<Void>> deleteAlbum(
            final @RequestParam Long albumId,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userEmail = userDetails.getUsername();
        albumService.removeAlbum(userEmail, albumId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        albumId + "번 album을 정상적으로 삭제했습니다.",
                        null
                )
        );
    }
}
