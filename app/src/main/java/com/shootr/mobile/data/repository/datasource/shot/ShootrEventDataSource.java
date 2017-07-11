package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.entity.ShootrEventEntity;

public interface ShotEventDataSource {

    void clickLink(ShootrEventEntity shootrEventEntity);

    void viewHighlightedShot(ShootrEventEntity shootrEventEntity);

    void shotDetailViewed(ShootrEventEntity shootrEventEntity);

    void sendShotEvents();

    void deleteShotEvents();
}
