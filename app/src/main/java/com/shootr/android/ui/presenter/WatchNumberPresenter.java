package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.bus.StreamChanged;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.WatchNumberInteractor;
import com.shootr.android.ui.views.WatchNumberView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import javax.inject.Inject;

public class WatchNumberPresenter implements Presenter, WatchUpdateRequest.Receiver, StreamChanged.Receiver {

    private final Bus bus;
    private final WatchNumberInteractor watchNumberInteractor;

    private WatchNumberView watchNumberView;
    private String idStream;

    @Inject public WatchNumberPresenter(@Main Bus bus, WatchNumberInteractor watchNumberInteractor) {
        this.bus = bus;
        this.watchNumberInteractor = watchNumberInteractor;
    }

    public void setView(WatchNumberView watchNumberView) {
        this.watchNumberView = watchNumberView;
    }

    public void initialize(WatchNumberView watchNumberView, String idStream) {
        this.setView(watchNumberView);
        this.idStream = idStream;
        this.retrieveData();
    }

    protected void retrieveData() {
        watchNumberInteractor.loadWatchNumber(new WatchNumberInteractor.Callback() {
            @Override public void onLoaded(Integer count) {
                setViewWathingCount(count);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO error handling
            }
        });
    }

    private void setViewWathingCount(Integer count) {
        if (count != WatchNumberInteractor.NO_STREAM) {
            watchNumberView.showWatchingPeopleCount(count);
        } else {
            watchNumberView.hideWatchingPeopleCount();
        }
    }

    @Override public void resume() {
        bus.register(this);
        this.retrieveData();
    }

    @Override public void pause() {
        bus.unregister(this);
    }

    @Subscribe
    @Override public void onWatchUpdateRequest(WatchUpdateRequest.Event event) {
        retrieveData();
    }

    @Subscribe
    @Override public void onStreamChanged(StreamChanged.Event event) {
        if (event.getNewStreamId() != null) {
            retrieveData();
        } else {
            watchNumberView.hideWatchingPeopleCount();
        }
    }

    public void onWatchNumberClick() {
        watchNumberView.navigateToStreamDetail(idStream);
    }
}
