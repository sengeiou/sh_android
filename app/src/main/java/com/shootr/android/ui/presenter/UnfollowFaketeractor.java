package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.UnfollowInteractor;
import javax.inject.Inject;

public class UnfollowFaketeractor {

    private final UnfollowInteractor unfollowInteractor;

    @Inject
    public UnfollowFaketeractor(UnfollowInteractor unfollowInteractor) {
        this.unfollowInteractor = unfollowInteractor;
    }

    protected void unfollow(String idUser, Interactor.CompletedCallback callback) {
        unfollowInteractor.unfollow(idUser, callback);
    }
}
