package me.snaptime.component.crawling;

import me.snaptime.component.crawling.provider.HaruFilm;
import me.snaptime.component.crawling.provider.PhotoProvider;
import org.springframework.stereotype.Component;

@Component
public class CrawlingComponentImpl implements CrawlingComponent {
    private final HaruFilm haruFilm;
    // private final LifeFourCuts lifeFourCuts;

    // 캐스팅을 사용해서 인터페이스를 하위타입으로 변경 받아 Bean에 주입한다.
    public CrawlingComponentImpl(PhotoProvider provider) {
        this.haruFilm = (HaruFilm) provider;
        // this.lifeFourCuts = (LifeFourCuts) provider;
    }

    @Override
    public byte[] getImageFromHaruFilm(String page_url) {
        String image_url = haruFilm.crawlingImageURL(page_url);
        return haruFilm.getImageBytes(image_url);
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
