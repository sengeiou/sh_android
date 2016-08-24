package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.repository.datasource.shot.ShotEventDataSource;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.ShotEventRepository;
import javax.inject.Inject;

public class LocalShotEventRepository implements ShotEventRepository {

    private final ShotEventDataSource shotEventDataSource;

    @Inject
    public LocalShotEventRepository(@Local ShotEventDataSource shotEventDataSource) {
        this.shotEventDataSource = shotEventDataSource;
    }

    @Override public void clickLink(String idShot) {

    }

    @Override public void viewHighlightedShot(String idShot) {

    }

    @Override public void shotDetailViewed(String idShot) {

    }

    @Override public void sendShotEvents() {
        //TODO: Not implemented
    }

    @Override public void deleteShotEvents() {
        shotEventDataSource.deleteShotEvents();
    }
}
