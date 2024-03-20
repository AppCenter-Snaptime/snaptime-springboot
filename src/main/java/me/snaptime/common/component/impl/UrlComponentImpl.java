package me.snaptime.common.component.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.component.UrlComponent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlComponentImpl implements UrlComponent {
    private final HttpServletRequest request;

    @Override
    public String makePhotoURL(String fileName, boolean isEncrypted) {
        return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() +
                "/photo?fileName=" + fileName + "&isEncrypted=" + isEncrypted;
    }

    @Override
    public String makeProfileURL(Long id) {
        return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() +
                "/profilePhotos/"+id;
    }
}
