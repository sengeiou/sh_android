package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.SuggestedPeopleView;
import javax.inject.Inject;

public class SuggestedPeoplePresenter implements Presenter {

    private SuggestedPeopleView suggestedPeopleView;

    @Inject public SuggestedPeoplePresenter() {
    }

    protected void setView(SuggestedPeopleView suggestedPeopleView) {
        this.suggestedPeopleView = suggestedPeopleView;
    }

    public void initialize(SuggestedPeopleView suggestedPeopleView) {
        setView(suggestedPeopleView);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
