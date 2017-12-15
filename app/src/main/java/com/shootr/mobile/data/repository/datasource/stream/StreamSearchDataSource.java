package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.StreamSearchEntity;

public interface StreamSearchDataSource {

    StreamSearchEntity getStreamResult(String idStream);

    void mute(String idStream);

    void unmute(String idStream);

    void follow(String idStream);

    void unfollow(String idStream);
}
