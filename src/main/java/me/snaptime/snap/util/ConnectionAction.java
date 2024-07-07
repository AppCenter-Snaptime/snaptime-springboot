package me.snaptime.snap.util;

import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ConnectionAction {
    public static byte[] getImage(String provider_url, String image_url) {
        try {
            URL imageURL = new URL("http://"+ provider_url + image_url);
            URLConnection connection = imageURL.openConnection();
            InputStream inputStream = connection.getInputStream();
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.URL_HAVING_PROBLEM);
        }
    }
}
