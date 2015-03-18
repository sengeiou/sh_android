package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.EventsWatchedCountInteractor;
import com.shootr.android.domain.interactor.event.WatchNumberInteractor;
import com.shootr.android.ui.views.WatchingRequestView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import javax.inject.Inject;

public class WatchNumberPresenter implements Presenter {

    private final Bus bus;
    private final WatchNumberInteractor watchNumberInteractor;

    private WatchingRequestView watchingRequestView;
    private Integer peopleWatchingCount;

    @Inject public WatchNumberPresenter(@Main Bus bus, WatchNumberInteractor watchNumberInteractor) {
        this.bus = bus;
        this.watchNumberInteractor = watchNumberInteractor;
    }

    public void initialize(WatchingRequestView watchingRequestView) {
        this.watchingRequestView = watchingRequestView;
        this.retrieveData();
    }

    private void retrieveData() {
        watchNumberInteractor.loadWatchNumber(new EventsWatchedCountInteractor.Callback() {
            @Override public void onLoaded(Integer count) {
                onNumberReceived(count);
            }
        }, new Interactor.InteractorErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO error handling
            }
        });
    }

    //TODO...
    @Subscribe public void onRequestWatchByPush(WatchUpdateRequest.Event event) {
        retrieveData();
    }

    public void onNumberReceived(Integer count) {
        peopleWatchingCount = count;
        setViewWathingCount();
    }

    public void menuCreated() {
        if (peopleWatchingCount != null) {
            setViewWathingCount();
        }
    }

    private void setViewWathingCount() {
        if (peopleWatchingCount != WatchNumberInteractor.NO_EVENT) {
            watchingRequestView.setWatchingPeopleCount(peopleWatchingCount);
        } else {
            watchingRequestView.hideWatchingPeopleCount();
        }
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
