package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.WatchUpdateRequest;
import com.shootr.mobile.domain.interactor.stream.WatchNumberInteractor;
import com.shootr.mobile.ui.views.WatchNumberView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import javax.inject.Inject;

public class WatchNumberPresenter implements Presenter, WatchUpdateRequest.Receiver {

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

    protected void retrieveData(boolean localOnly) {
        watchNumberInteractor.loadWatchersNumber(idStream, localOnly, new WatchNumberInteractor.Callback() {
            @Override public void onLoaded(Long[] count) {
                setViewWathingCount(count);
            }
        });
    }

    private void setViewWathingCount(Long[] count) {
        if (count[WatchNumberInteractor.CONNECTED] > 0 || count[WatchNumberInteractor.FOLLOWERS] > 0) {
            watchNumberView.showWatchingPeopleCount(count);
        } else {
            watchNumberView.hideWatchingPeopleCount();
        }
    }

    @Override public void resume() {
        bus.register(this);
        if (hasBeenPaused) {
            this.retrieveData(false);
        }
    }

    @Override public void pause() {
        bus.unregister(this);
        hasBeenPaused = true;
    }

    @Subscribe @Override public void onWatchUpdateRequest(WatchUpdateRequest.Event event) {
        retrieveData(event.isLocalOnly());
    }
}
