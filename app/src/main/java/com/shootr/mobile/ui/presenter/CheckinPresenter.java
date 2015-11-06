package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.PerformCheckinInteractor;
import com.shootr.mobile.ui.views.CheckinView;
import javax.inject.Inject;

public class CheckinPresenter implements Presenter {

    private final PerformCheckinInteractor performCheckinInteractor;

    private CheckinView checkinView;
    private String idStream;


    @Inject public CheckinPresenter(PerformCheckinInteractor performCheckinInteractor) {
        this.performCheckinInteractor = performCheckinInteractor;
    }

    public void setView(CheckinView checkinView) {
        this.checkinView = checkinView;
    }

    protected void setStreamId(String idStream) {
        this.idStream = idStream;
    }

    public void initialize(CheckinView checkinView, String idStream) {
        this.setStreamId(idStream);
        this.setView(checkinView);
    }

    public void checkIn() {
        checkinView.showCheckinConfirmation();
    }

    public void confirmCheckin() {
        checkinView.disableCheckinButton();
        performCheckinInteractor.performCheckin(idStream, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                checkinView.showCheckinDone();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                checkinView.enableCheckinButton();
                checkinView.showCheckinError();
            }
        });
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
