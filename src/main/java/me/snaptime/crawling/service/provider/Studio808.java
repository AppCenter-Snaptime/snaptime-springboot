package me.snaptime.crawling.service.provider;

import me.snaptime.util.JsoupAction;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class Studio808 extends AbstractPhotoProvider{
    @Override
    public String crawlingImageURL(String page_url) {
        Document crawledPage = JsoupAction.getDocument(page_url);
        return JsoupAction.returnSrcBySelectedElements(crawledPage, "div.main_cont > img");
    }

    @Override
    public String getHostname() {
        return "studio808.mx2.co.kr";
    }
}
