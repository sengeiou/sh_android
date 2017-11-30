package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.entity.ShootrEventEntity;

public interface ShootrEventDataSource {

    void clickLink(ShootrEventEntity shootrEventEntity);

    void viewHighlightedShot(ShootrEventEntity shootrEventEntity);

    void shotDetailViewed(ShootrEventEntity shootrEventEntity);

    void sendShotEvents();

    void viewUserProfileEvent(ShootrEventEntity shootrEventEntity);

    void deleteShootrEvents();

    void getShootrEvents();

    void timelineViewed(ShootrEventEntity shootrEvent);
}
