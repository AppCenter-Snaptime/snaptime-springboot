package me.snaptime.component.crawling;

public interface CrawlingComponent {
    byte[] getImage(String providerName, String page_url);
}
