package com.shootr.mobile.domain.repository;

public interface ShotEventRepository {

    void clickLink(String idShot);

    void viewHighlightedShot(String idShot);

    void shotDetailViewed(String idShot);

    void sendShotEvents();

    void deleteShotEvents();
}
