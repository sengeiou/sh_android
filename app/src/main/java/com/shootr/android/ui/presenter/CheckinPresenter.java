package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.GetCheckinStatusInteractor;
import com.shootr.android.domain.interactor.user.PerformCheckinInteractor;
import com.shootr.android.ui.views.CheckinView;
import javax.inject.Inject;

public class CheckinPresenter implements Presenter {

    private final GetCheckinStatusInteractor getCheckinStatusInteractor;
    private final PerformCheckinInteractor performCheckinInteractor;

    private CheckinView checkinView;
    private Boolean checkedInEvent;
    protected boolean showingCheckinButton;
    private String idEvent;

    @Inject public CheckinPresenter(GetCheckinStatusInteractor getCheckinStatusInteractor,
      PerformCheckinInteractor performCheckinInteractor) {
        this.getCheckinStatusInteractor = getCheckinStatusInteractor;
        this.performCheckinInteractor = performCheckinInteractor;
    }

    protected void setView(CheckinView checkinView) {
        this.checkinView = checkinView;
    }

    public void initialize(CheckinView checkinView, String idEvent) {
        this.idEvent = idEvent;
        this.setView(checkinView);
        this.loadCheckinStatus();
    }

    private void loadCheckinStatus() {
        getCheckinStatusInteractor.loadCheckinStatus(idEvent, new Interactor.Callback<Boolean>() {
            @Override public void onLoaded(Boolean checkedIn) {
                checkedInEvent = checkedIn;
                onCheckinStatusLoaded();
            }
        });
    }

    private void onCheckinStatusLoaded() {
        if (!checkedInEvent) {
            this.showViewCheckinButton();
        } else {
            checkinView.showCheckedIn();
        }
    }

    public void toolbarClick() {
        if (checkedInEvent != null) {
            if (showingCheckinButton) {
                hideViewCheckinButton();
                showViewCheckedInIfCheckedIn();
            } else {
                showViewCheckinButton();
                checkinView.hideCheckedIn();
            }
        }
    }

    private void showViewCheckedInIfCheckedIn() {
        if (checkedInEvent) {
            checkinView.showCheckedIn();
        }
    }

    public void checkinClick() {
        if (checkedInEvent) {
            checkOut();
        } else {
            checkIn();
        }
    }

    protected void checkIn() {
        checkinView.showCheckinLoading();
        performCheckinInteractor.performCheckin(idEvent, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                checkinView.hideCheckinLoading();
                checkinView.hideCheckinView();
                checkinView.showCheckedIn();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO veremos
            }
        });
    }

    private void checkOut() {
        //TODO waiting for the interactor
    }

    private void showViewCheckinButton() {
        showingCheckinButton = true;
        checkinView.showCheckinButton();
    }

    private void hideViewCheckinButton() {
        showingCheckinButton = false;
        checkinView.hideCheckinView();
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
