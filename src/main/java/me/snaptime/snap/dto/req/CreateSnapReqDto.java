package me.snaptime.snap.dto.req;

import org.springframework.web.multipart.MultipartFile;

public record CreateSnapReqDto(
        String oneLineJournal,
        MultipartFile multipartFile
) {
}
