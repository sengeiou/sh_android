package com.shootr.mobile.data.repository.datasource.stream;

import android.support.v4.util.ArrayMap;
import com.shootr.mobile.data.entity.StreamSearchEntity;
import com.shootr.mobile.db.manager.StreamManager;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class DatabaseMemoryStreamSearchDataSource implements StreamSearchDataSource {

    private final StreamManager streamManager;
    private final Map<String, StreamSearchEntity> lastStreamSearchResults;

    @Inject
    public DatabaseMemoryStreamSearchDataSource(StreamManager streamManager) {
        this.streamManager = streamManager;
        lastStreamSearchResults = new ArrayMap<>();
    }

    public void setLastSearchResults(List<StreamSearchEntity> streamSearchEntities) {
        lastStreamSearchResults.clear();
        try {
            for (StreamSearchEntity streamSearchEntity : streamSearchEntities) {
                lastStreamSearchResults.put(streamSearchEntity.getIdStream(), streamSearchEntity);
            }
        } catch (ArrayIndexOutOfBoundsException error) {
            /* no-op */
        } catch (Exception error) {
            /* no-op */
        }
    }

    @Override public StreamSearchEntity getStreamResult(String idStream) {
        StreamSearchEntity streamFromDefaultList = streamManager.getStreamSearchResultById(idStream);
        if (streamFromDefaultList != null) {
            return streamFromDefaultList;
        } else {
            return lastStreamSearchResults.get(idStream);
        }
    }

    @Override public void mute(String idStream) {
        streamManager.muteStreamSearchResult(idStream);
    }

    @Override public void unmute(String idStream) {
        streamManager.unMuteStreamSearchResult(idStream);
    }

    @Override public void follow(String idStream) {
        streamManager.followStreamSearchResult(idStream);
    }

    @Override public void unfollow(String idStream) {
        streamManager.unFollowStreamSearchResult(idStream);
    }
}
