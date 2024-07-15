package me.snaptime.crawling.service.provider;

import me.snaptime.util.JsoupAction;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class PhotoSignature extends AbstractPhotoProvider {
    @Override
    public String crawlingImageURL(String page_url) {
        Document crawledPage = JsoupAction.getDocument(page_url);
        // URL에서 index.html 삭제
        String hostName = page_url.replace("index.html", "");
        // URL에서 http:// 삭제
        return  hostName.replace("http://", "") + JsoupAction.returnHrefBySelectedElements(crawledPage, "body > div > div.download-buttons > a:nth-child(1)");
    }

    @Override
    public String getHostname() {
        // CrawlingImageURL에서 ConnectionAction.getImage에 필요한 hostName까지 만들었음으로 공백으로 유지
        return "";
    }
}
