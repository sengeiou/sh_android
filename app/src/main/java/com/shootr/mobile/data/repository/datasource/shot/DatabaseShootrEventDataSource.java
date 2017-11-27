package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.entity.ShootrEventEntity;
import com.shootr.mobile.db.manager.ShootrEventManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseShootrEventDataSource implements ShootrEventDataSource {

    private final ShootrEventManager shootrEventManager;
    public static final String METHOD_NOT_VALID_FOR_DATABASE = "Method not valid for database";

    @Inject public DatabaseShootrEventDataSource(ShootrEventManager shootrEventManager) {
        this.shootrEventManager = shootrEventManager;
    }

    @Override public void clickLink(ShootrEventEntity shootrEventEntity) {
        shootrEventManager.clickLink(shootrEventEntity);
    }

    @Override public void viewHighlightedShot(ShootrEventEntity shootrEventEntity) {
        shootrEventManager.viewHighlightedShot(shootrEventEntity);
    }

    @Override public void shotDetailViewed(ShootrEventEntity shootrEventEntity) {
        shootrEventManager.shotDetailViewed(shootrEventEntity);
    }

    @Override public void sendShotEvents() {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_DATABASE);
    }

    @Override public void viewUserProfileEvent(ShootrEventEntity shootrEventEntity) {
        shootrEventManager.viewUserProfileEvent(shootrEventEntity);
    }

    public List<ShootrEventEntity> getEvents() {
        return shootrEventManager.getEvents();
    }

    @Override public void deleteShootrEvents() {
        shootrEventManager.deleteShotEvents();
    }

    @Override public void getShootrEvents() {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_DATABASE);
    }

    @Override public void timelineViewed(ShootrEventEntity shootrEvent) {
        shootrEventManager.timelineViewed(shootrEvent);
    }
}
