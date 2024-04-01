package me.snaptime.snap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.snap.data.dto.req.CreateAlbumReqDto;
import me.snaptime.snap.data.dto.res.FindAlbumResDto;
import me.snaptime.snap.data.dto.res.GetAllAlbumListResDto;
import me.snaptime.snap.data.dto.res.FindAllAlbumsResDto;
import me.snaptime.snap.service.AlbumService;
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
    public ResponseEntity<CommonResponseDto<List<FindAllAlbumsResDto>>> findAllAlbumsByLoginId(
            final @AuthenticationPrincipal UserDetails userDetails
            ) {
        String uId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new CommonResponseDto<>(
                                "사용자의 앨범 목록(썸네일 포함)이 정상적으로 불러와졌습니다.",
                                albumService.findAllAlbumsByLoginId(uId)
                        )
                );
    }

    @Operation(summary = "Album 생성", description = "사용자의 Album을 생성합니다.")
    @PostMapping
    public ResponseEntity<CommonResponseDto<Void>> createAlbum(
            final @RequestBody CreateAlbumReqDto createAlbumReqDto,
            final @AuthenticationPrincipal UserDetails userDetails
            ) {
        String uId = userDetails.getUsername();
        albumService.createAlbum(createAlbumReqDto, uId);
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
        String uId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "사용자의 앨범 목록(앨범의 이름들)을 정상적으로 가져왔습니다.",
                        albumService.getAlbumListByLoginId(uId)
                )
        );
    }

    @Operation(summary = "한 Album의 내용을 전부 가져오기", description = "Album의 내용을 전부 가져옵니다.")
    @GetMapping("/{album_id}")
    public ResponseEntity<CommonResponseDto<FindAlbumResDto>> findAlbum(
            final @RequestParam("album_id") Long album_id,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "한 Album의 내용을 전부 가져오는데 성공했습니다.",
                        albumService.findAlbum(uId, album_id)
                )
        );
    }




}
