package me.snaptime.snap.component.crawling.provider;

import me.snaptime.snap.util.ConnectionAction;
import me.snaptime.snap.util.JsoupAction;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class OnePercent implements PhotoProvider {

    @Override
    public String crawlingImageURL(String page_url) {
        Document crawledPage = JsoupAction.getDocument(page_url);
        return JsoupAction.returnSrcBySelectedElements(crawledPage, "div.main_cont > img");
    }

    @Override
    public byte[] getImageBytes(String image_url) {
        return ConnectionAction.getImage("bc1.mx2.co.kr", image_url);
    }
}
