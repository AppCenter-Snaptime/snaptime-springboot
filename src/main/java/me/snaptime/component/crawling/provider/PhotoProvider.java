package me.snaptime.component.crawling.provider;

public interface PhotoProvider {
    String crawlingImageURL(String page_url);
    byte[] getImageBytes(String image_url);
}
