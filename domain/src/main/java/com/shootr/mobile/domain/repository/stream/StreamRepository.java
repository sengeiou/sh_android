package com.shootr.mobile.domain.repository.stream;

import com.shootr.mobile.domain.model.stream.Stream;
import java.util.List;

public interface StreamRepository {

    Stream getStreamById(String idStream, String[] types);

    List<Stream> getStreamsByIds(List<String> streamIds, String[] types);

    Stream putStream(Stream stream);

    void removeStream(String idStream);

    void restoreStream(String idStream);

    String getLastTimeFiltered(String idStream);

    void putLastTimeFiltered(String idStream, String lastTimeFiltered);

    void mute(String idStream);

    void unmute(String idStream);

    void follow(String idStream);

    void unfollow(String idStream);
}
