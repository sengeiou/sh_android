package com.shootr.mobile.data.repository.remote.cache;

import com.fewlaps.quitnowcache.QNCache;
import com.shootr.mobile.domain.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StreamCache {

    public static final int GET_STREAM_KEEP_ALIVE_SECONDS =  60 * 60 * 1000;
    private final QNCache streamCache;

    @Inject
    public StreamCache(QNCache streamCache) {
        this.streamCache = streamCache;
    }

    public Stream getStreamById(String idStream) {
        Stream stream = streamCache.get(idStream);
        if (stream != null) {
            return stream;
        } else {
            return null;
        }
    }

    public void putStream(Stream stream) {
        streamCache.set(stream.getId(), stream, GET_STREAM_KEEP_ALIVE_SECONDS);
    }

}
