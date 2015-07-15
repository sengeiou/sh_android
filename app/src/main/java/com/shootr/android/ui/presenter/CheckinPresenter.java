package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.PerformCheckinInteractor;
import com.shootr.android.ui.views.CheckinView;
import javax.inject.Inject;

public class CheckinPresenter implements Presenter {

    private final PerformCheckinInteractor performCheckinInteractor;

    private CheckinView checkinView;
    private String idEvent;


    @Inject public CheckinPresenter(PerformCheckinInteractor performCheckinInteractor) {
        this.performCheckinInteractor = performCheckinInteractor;
    }

    public void setView(CheckinView checkinView) {
        this.checkinView = checkinView;
    }

    protected void setEventId(String idEvent) {
        this.idEvent = idEvent;
    }

    public void initialize(CheckinView checkinView, String idEvent) {
        this.setEventId(idEvent);
        this.setView(checkinView);
    }

    public void checkIn() {
        checkinView.showCheckinConfirmation();
    }

    public void confirmCheckin() {
        checkinView.disableCheckinButton();
        performCheckinInteractor.performCheckin(idEvent, new Interactor.CompletedCallback() {
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
