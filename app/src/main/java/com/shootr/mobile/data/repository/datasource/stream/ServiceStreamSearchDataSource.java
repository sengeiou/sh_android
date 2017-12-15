package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.StreamSearchEntity;
import javax.inject.Inject;

public class ServiceStreamSearchDataSource implements StreamSearchDataSource {

    @Inject public ServiceStreamSearchDataSource() {
    }

    @Override public StreamSearchEntity getStreamResult(String idStream) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public void mute(String idStream) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public void unmute(String idStream) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public void follow(String idStream) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public void unfollow(String idStream) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }
}
