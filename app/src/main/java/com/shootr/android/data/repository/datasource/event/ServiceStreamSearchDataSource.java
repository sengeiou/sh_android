package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.StreamSearchEntity;
import java.util.List;
import javax.inject.Inject;

public class ServiceStreamSearchDataSource implements StreamSearchDataSource {

    @Inject public ServiceStreamSearchDataSource() {
    }

    @Override public List<StreamSearchEntity> getDefaultStreams(String locale) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public void putDefaultStreams(List<StreamSearchEntity> transform) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public void deleteDefaultStreams() {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public StreamSearchEntity getStreamResult(String idEvent) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }
}
