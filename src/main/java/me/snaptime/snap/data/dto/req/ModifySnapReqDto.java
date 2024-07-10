package me.snaptime.snap.data.dto.req;

import org.springframework.web.multipart.MultipartFile;

public record ModifySnapReqDto(
        String oneLineJournal,
        MultipartFile multipartFile
) {
}
