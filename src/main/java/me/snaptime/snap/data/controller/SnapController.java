package me.snaptime.snap.data.controller;

import lombok.RequiredArgsConstructor;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.service.SnapService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/snap")
@RequiredArgsConstructor
public class SnapController {
    private final SnapService snapService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void createSnap(final @ModelAttribute CreateSnapReqDto createSnapReqDto) {
        snapService.createSnap(createSnapReqDto, null);
    }

}
