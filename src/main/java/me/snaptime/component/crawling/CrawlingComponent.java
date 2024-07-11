package me.snaptime.component.crawling;

public interface CrawlingComponent {
    byte[] getImageFromHaruFilm(String page_url);
    byte[] getImageFromOnePercent(String page_url);
    String getImageFromLifeFourCuts(String page_url);
    String getImageFromPhotoism(String page_url);
}
