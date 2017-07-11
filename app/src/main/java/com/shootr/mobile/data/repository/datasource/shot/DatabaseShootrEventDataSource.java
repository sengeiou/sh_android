package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.entity.ShootrEventEntity;
import com.shootr.mobile.db.manager.ShotEventManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseShotEventDataSource implements ShotEventDataSource {

    private final ShotEventManager shotEventManager;
    public static final String METHOD_NOT_VALID_FOR_DATABASE = "Method not valid for database";

    @Inject public DatabaseShotEventDataSource(ShotEventManager shotEventManager) {
        this.shotEventManager = shotEventManager;
    }

    @Override public void clickLink(ShootrEventEntity shootrEventEntity) {
        shotEventManager.clickLink(shootrEventEntity);
    }

    @Override public void viewHighlightedShot(ShootrEventEntity shootrEventEntity) {
        shotEventManager.viewHighlightedShot(shootrEventEntity);
    }

    @Override public void shotDetailViewed(ShootrEventEntity shootrEventEntity) {
        shotEventManager.shotDetailViewed(shootrEventEntity);
    }

    @Override public void sendShotEvents() {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_DATABASE);
    }

    public List<ShootrEventEntity> getEvents() {
        return shotEventManager.getEvents();
    }

    @Override public void deleteShotEvents() {
        shotEventManager.deleteShotEvents();
    }
}
