package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.FollowInteractor;
import javax.inject.Inject;

public class FollowFaketeractor {

    private final FollowInteractor followInteractor;

    @Inject
    public FollowFaketeractor(FollowInteractor followInteractor) {
        this.followInteractor = followInteractor;
    }

    protected void follow(String idUser, Interactor.CompletedCallback callback) {
        followInteractor.follow(idUser, callback);
    }

}
