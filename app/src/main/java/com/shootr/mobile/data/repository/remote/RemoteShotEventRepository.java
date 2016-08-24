package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.repository.datasource.shot.ShotEventDataSource;
import com.shootr.mobile.domain.repository.ShotEventRepository;
import javax.inject.Inject;

public class RemoteShotEventRepository implements ShotEventRepository {

    private final ShotEventDataSource shotEventDataSource;

    @Inject public RemoteShotEventRepository(ShotEventDataSource shotEventDataSource) {
        this.shotEventDataSource = shotEventDataSource;
    }

    @Override public void clickLink(String idShot) {

    }

    @Override public void viewHighlightedShot(String idShot) {

    }

    @Override public void shotDetailViewed(String idShot) {

    }

    @Override public void sendShotEvents() {
        shotEventDataSource.sendShotEvents();
    }

    @Override public void deleteShotEvents() {
        //TODO: Not implemented on remote
    }
}
