package me.snaptime.crawling.service.provider;

public interface PhotoProvider {
    String crawlingImageURL(String page_url);
    byte[] getImageBytes(String image_url);
    String getHostname();
}
