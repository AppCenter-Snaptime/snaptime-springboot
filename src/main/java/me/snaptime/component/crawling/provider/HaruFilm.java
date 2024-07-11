package me.snaptime.component.crawling.provider;

import me.snaptime.util.ConnectionAction;
import me.snaptime.util.JsoupAction;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;


@Component
public class HaruFilm implements PhotoProvider {
    @Override
    public String crawlingImageURL(String page_url) {
        Document crawledPage = JsoupAction.getDocument(page_url);
        return JsoupAction.returnSrcBySelectedElements(crawledPage, "div.main_cont > img");
    }

    @Override
    public byte[] getImageBytes(String image_url) {
        return ConnectionAction.getImage("haru9.mx2.co.kr", image_url);
    }
}
