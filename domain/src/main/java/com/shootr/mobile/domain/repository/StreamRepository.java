package com.shootr.mobile.domain.repository;

import java.util.List;

public interface StreamRepository {

    com.shootr.mobile.domain.Stream getStreamById(String idStream);

    List<com.shootr.mobile.domain.Stream> getStreamsByIds(List<String> streamIds);

    com.shootr.mobile.domain.Stream putStream(com.shootr.mobile.domain.Stream stream);

    com.shootr.mobile.domain.Stream putStream(com.shootr.mobile.domain.Stream stream, boolean notify);

    void shareStream(String idStream);

    void removeStream(String idStream);

    void restoreStream(String idStream);

    com.shootr.mobile.domain.Stream getBlogStream(String country, String language);

    com.shootr.mobile.domain.Stream getHelpStream(String country, String language);
}
