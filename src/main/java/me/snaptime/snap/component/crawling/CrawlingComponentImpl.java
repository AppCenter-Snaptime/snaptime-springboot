package me.snaptime.snap.component.crawling;
import me.snaptime.snap.component.crawling.provider.HaruFilm;
import me.snaptime.snap.component.crawling.provider.OnePercent;
import me.snaptime.snap.component.crawling.provider.PhotoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;



@Component
public class CrawlingComponentImpl implements CrawlingComponent {
    private final HaruFilm haruFilm;
    private final OnePercent onePercent;

    @Autowired
    public CrawlingComponentImpl(@Qualifier("haruFilm") PhotoProvider haruFilm,
                                 @Qualifier("onePercent") PhotoProvider onePercent) {
        this.haruFilm = (HaruFilm) haruFilm;
        this.onePercent = (OnePercent) onePercent;
    }

    @Override
    public byte[] getImageFromHaruFilm(String page_url) {
        String image_url = haruFilm.crawlingImageURL(page_url);
        return haruFilm.getImageBytes(image_url);
    }

    @Override
    public byte[] getImageFromOnePercent(String page_url) {
        String image_url = onePercent.crawlingImageURL(page_url);
        return onePercent.getImageBytes(image_url);
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
