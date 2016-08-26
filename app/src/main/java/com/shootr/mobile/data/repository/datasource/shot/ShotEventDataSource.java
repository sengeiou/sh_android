package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.entity.ShotEventEntity;

public interface ShotEventDataSource {

    void clickLink(ShotEventEntity shotEventEntity);

    void viewHighlightedShot(ShotEventEntity shotEventEntity);

    void shotDetailViewed(ShotEventEntity shotEventEntity);

    void sendShotEvents();

    void deleteShotEvents();
}
