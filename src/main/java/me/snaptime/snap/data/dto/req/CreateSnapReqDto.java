package me.snaptime.snap.data.dto.req;

import org.springframework.web.multipart.MultipartFile;

public record CreateSnapReqDto(
        String oneLineJournal,
        MultipartFile multipartFile
) {
}
