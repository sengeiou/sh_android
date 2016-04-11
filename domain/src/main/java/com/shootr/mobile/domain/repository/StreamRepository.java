package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.Stream;

import java.util.List;

public interface StreamRepository {

    Stream getStreamById(String idStream);

    List<Stream> getStreamsByIds(List<String> streamIds);

    Stream putStream(Stream stream);

    Stream putStream(Stream stream, boolean notify, boolean notifyStreamMessage);

    void shareStream(String idStream);

    void removeStream(String idStream);

    void restoreStream(String idStream);

    Stream getBlogStream(String country, String language);

    Stream getHelpStream(String country, String language);
}
