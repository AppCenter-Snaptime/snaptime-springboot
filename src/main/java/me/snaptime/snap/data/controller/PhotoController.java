package me.snaptime.snap.data.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.snap.service.PhotoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
@Tag(name = "[Photo] Photo API")
public class PhotoController {
    private final PhotoService photoService;
}
