package com.shootr.mobile.domain.repository.stream;

import com.shootr.mobile.domain.exception.InvalidYoutubeVideoUrlException;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.model.TimelineReposition;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.PromotedTiers;
import java.util.List;

public interface StreamRepository {

    Stream getStreamById(String idStream, String[] types);

    List<Stream> getStreamsByIds(List<String> streamIds, String[] types);

    Stream putStream(Stream stream) throws InvalidYoutubeVideoUrlException;

    void removeStream(String idStream);

    void restoreStream(String idStream);

    String getLastTimeFiltered(String idStream);

    void putLastTimeFiltered(String idStream, String lastTimeFiltered);

    void mute(String idStream);

    void unmute(String idStream);

    void follow(String idStream);

    void unfollow(String idStream);

    void hide(String idStream);

    long getConnectionTimes(String idStream);

    void storeConnection(String idStream, long connections);

    LandingStreams getLandingStreams();

    void putLandingStreams(LandingStreams landingStreams);

    void putLastStreamVisit(String idStream, long timestamp);

    Long getLastStreamVisit(String idStream);

    StreamTimeline getCachedTimeline(String idStream, String filter);

    StreamTimeline getCachedNicestTimeline(String idStream, String filter, long period);

    void putTimelineReposition(TimelineReposition timelineReposition, String idStrea, String filter);

    TimelineReposition getTimelineReposition(String idStream, String filter);

    PromotedTiers getPromotedTiers();
}
