package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.model.shot.ShotEvent;

public interface ShotEventRepository {

    void clickLink(ShotEvent shotEvent);

    void viewHighlightedShot(ShotEvent shotEvent);

    void shotDetailViewed(ShotEvent shotEvent);

    void sendShotEvents();
}
