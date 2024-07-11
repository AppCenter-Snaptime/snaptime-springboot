package me.snaptime.component.crawling.provider;

import me.snaptime.util.JsoupAction;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;


@Component
public class HaruFilm extends AbstractPhotoProvider {
    @Override
    public String crawlingImageURL(String page_url) {
        Document crawledPage = JsoupAction.getDocument(page_url);
        return JsoupAction.returnSrcBySelectedElements(crawledPage, "div.main_cont > img");
    }

    @Override
    public String getHostname() {
        return "haru9.mx2.co.kr";
    }
}
