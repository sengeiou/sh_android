package com.shootr.android.domain.repository;

import com.shootr.android.domain.Stream;
import java.util.List;

public interface StreamRepository {

    Stream getStreamById(String idStream);

    List<Stream> getStreamsByIds(List<String> streamIds);

    Stream putStream(Stream stream);

    Stream putStream(Stream stream, boolean notify);

    Integer getListingCount(String idUser);

    void shareStream(String idStream);
}
