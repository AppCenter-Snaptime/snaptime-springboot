package me.snaptime.component.crawling.provider;

public abstract class Provider {
    abstract String getCrawlingImageURL(String page_url);
    abstract byte[] getImageByte(String image_url);
}
