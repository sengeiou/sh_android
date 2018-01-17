package com.shootr.mobile.domain.repository.stream;

import com.shootr.mobile.domain.model.stream.LandingStreams;
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

    long getConnectionTimes(String idStream);

    void storeConnection(String idStream, long connections);

    LandingStreams getLandingStreams();

    void putLandingStreams(LandingStreams landingStreams);

    void putLastStreamVisit(String idStream, long timestamp);

    Long getLastStreamVisit(String idStream);
}
