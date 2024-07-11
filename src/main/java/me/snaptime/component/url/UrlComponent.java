package me.snaptime.component.url;

public interface UrlComponent {
    String makePhotoURL(String fileName, boolean isEncrypted);
    String makeProfileURL(Long id);
}
