package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.StreamChanged;
import com.shootr.mobile.domain.bus.WatchUpdateRequest;
import com.shootr.mobile.domain.interactor.stream.WatchNumberInteractor;
import com.shootr.mobile.ui.views.WatchNumberView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import javax.inject.Inject;

public class WatchNumberPresenter implements Presenter, WatchUpdateRequest.Receiver, StreamChanged.Receiver {

    private final Bus bus;
    private final WatchNumberInteractor watchNumberInteractor;

    private WatchNumberView watchNumberView;
    private String idStream;
    private boolean hasBeenPaused = false;

    @Inject public WatchNumberPresenter(@Main Bus bus, WatchNumberInteractor watchNumberInteractor) {
        this.bus = bus;
        this.watchNumberInteractor = watchNumberInteractor;
    }

    protected void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public void setView(WatchNumberView watchNumberView) {
        this.watchNumberView = watchNumberView;
    }

    public void initialize(WatchNumberView watchNumberView, String idStream) {
        this.setView(watchNumberView);
        this.setIdStream(idStream);
    }

    protected void retrieveData() {
        watchNumberInteractor.loadWatchersNumber(idStream, new WatchNumberInteractor.Callback() {
            @Override public void onLoaded(Integer[] count) {
                setViewWathingCount(count);
            }
        });
    }

    private void setViewWathingCount(Integer[] count) {
        if (count[WatchNumberInteractor.WATCHERS] > 0) {
            handleWatchers(count);
        } else {
            watchNumberView.hideWatchingPeopleCount();
        }
    }

    private void handleWatchers(Integer[] count) {
        if (count[WatchNumberInteractor.FRIENDS] > 0) {
            watchNumberView.showWatchingPeopleCount(count);
        } else {
            watchNumberView.showParticipantsCount(count);
        }
    }

    @Override public void resume() {
        bus.register(this);
        if (hasBeenPaused) {
            this.retrieveData();
        }
    }

    @Override public void pause() {
        bus.unregister(this);
        hasBeenPaused = true;
    }

    @Subscribe @Override public void onWatchUpdateRequest(WatchUpdateRequest.Event event) {
        retrieveData();
    }

    @Subscribe @Override public void onStreamChanged(StreamChanged.Event event) {
        if (event.getNewStreamId() != null) {
            retrieveData();
        } else {
            watchNumberView.hideWatchingPeopleCount();
        }
    }
}
