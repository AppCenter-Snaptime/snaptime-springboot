package me.snaptime.common.component;

public interface UrlComponent {
    String makePhotoURL(String fileName, boolean isEncrypted);
    String makeProfileURL(Long id);
}
