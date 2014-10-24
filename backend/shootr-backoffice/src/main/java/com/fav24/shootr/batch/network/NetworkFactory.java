package com.fav24.shootr.batch.network;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Service
public class NetworkFactory {

    private final String charset = "UTF-8";

    public InputStream getInputStreamFromUrl(String requestUrl) throws IOException {
        InputStream response;
        URLConnection connection = new URL(requestUrl).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        response = connection.getInputStream();
        return response;
    }


}
