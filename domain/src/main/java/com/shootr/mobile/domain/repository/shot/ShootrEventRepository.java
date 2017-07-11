package com.shootr.mobile.domain.repository.shot;

import com.shootr.mobile.domain.model.shot.ShootrEvent;

public interface ShootrEventRepository {

    void clickLink(ShootrEvent shootrEvent);

    void viewHighlightedShot(ShootrEvent shootrEvent);

    void shotDetailViewed(ShootrEvent shootrEvent);

    void sendShotEvents();

    void sendUserProfileEvent();
}
