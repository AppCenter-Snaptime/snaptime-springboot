package me.snaptime.crawling.service;

public interface CrawlingService {
    byte[] getImage(String providerName, String page_url);
}
