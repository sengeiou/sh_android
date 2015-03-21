package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.bus.EventChanged;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.WatchNumberInteractor;
import com.shootr.android.ui.views.WatchNumberView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import javax.inject.Inject;

public class WatchNumberPresenter implements Presenter, WatchUpdateRequest.Receiver, EventChanged.Receiver {

    private final Bus bus;
    private final WatchNumberInteractor watchNumberInteractor;

    private WatchNumberView watchNumberView;

    @Inject public WatchNumberPresenter(@Main Bus bus, WatchNumberInteractor watchNumberInteractor) {
        this.bus = bus;
        this.watchNumberInteractor = watchNumberInteractor;
    }

    public void setView(WatchNumberView watchNumberView) {
        this.watchNumberView = watchNumberView;
    }

    public void initialize(WatchNumberView watchNumberView) {
        this.setView(watchNumberView);
        this.retrieveData();
    }

    protected void retrieveData() {
        watchNumberInteractor.loadWatchNumber(new WatchNumberInteractor.Callback() {
            @Override public void onLoaded(Integer count) {
                setViewWathingCount(count);
            }
        }, new Interactor.InteractorErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO error handling
            }
        });
    }

    private void setViewWathingCount(Integer count) {
        if (count != WatchNumberInteractor.NO_EVENT) {
            watchNumberView.showWatchingPeopleCount(count);
        } else {
            watchNumberView.hideWatchingPeopleCount();
        }
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }

    @Subscribe
    @Override public void onWatchUpdateRequest(WatchUpdateRequest.Event event) {
        retrieveData();
    }

    @Subscribe
    @Override public void onEventChanged(EventChanged.Event event) {
        if (event.getNewEventId() != null) {
            retrieveData();
        } else {
            watchNumberView.hideWatchingPeopleCount();
        }
    }
}
