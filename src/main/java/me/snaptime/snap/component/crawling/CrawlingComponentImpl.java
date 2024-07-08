package me.snaptime.snap.component.crawling;

import lombok.RequiredArgsConstructor;
import me.snaptime.snap.component.crawling.provider.HaruFilm;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlingComponentImpl implements CrawlingComponent {
    private final HaruFilm haruFilm;

    @Override
    public byte[] getImageFromHaruFilm(String page_url) {
        String image_url = haruFilm.getCrawling(page_url);
        return haruFilm.getImageByte(image_url);
    }

    @Override
    public String getImageFromLifeFourCuts(String page_url) {
        return "";
    }

    @Override
    public String getImageFromPhotoism(String page_url) {
        return "";
    }

    //....
}
