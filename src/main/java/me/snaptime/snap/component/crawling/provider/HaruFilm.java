package me.snaptime.snap.component.crawling.provider;

import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.util.JsoupAction;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Component
public class HaruFilm extends Provider {
    @Override
    public String getCrawling(String page_url) {
        Document crawledPage = JsoupAction.getDocument(page_url);
        return JsoupAction.returnSrcBySelectedElements(crawledPage, "div.main_cont > img");
    }

    @Override
    public byte[] getImageByte(String image_url) {
        try {
            URL imageURL = new URL("http://haru9.mx2.co.kr" + image_url);
            URLConnection connection = imageURL.openConnection();
            InputStream inputStream = connection.getInputStream();
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.SNAP_NOT_EXIST);
        }
    }
}
