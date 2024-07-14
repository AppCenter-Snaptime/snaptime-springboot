package me.snaptime.crawling.service.provider;

import me.snaptime.util.ConnectionAction;

public abstract class AbstractPhotoProvider implements PhotoProvider {
    @Override
    public byte[] getImageBytes(String image_url) {
        return ConnectionAction.getImage(getHostname(), image_url);
    }
}
