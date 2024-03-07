package me.snaptime.snap.data.dto.req;

import org.springframework.web.multipart.MultipartFile;

public record ModifySnapReqDto(
        Long id,
        String oneLineJournal,
        MultipartFile multipartFile,
        String album
) {
}
