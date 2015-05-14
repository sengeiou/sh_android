package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.GetCheckinStatusInteractor;
import com.shootr.android.domain.interactor.user.PerformCheckinInteractor;
import com.shootr.android.domain.interactor.user.PerformCheckoutInteractor;
import com.shootr.android.ui.views.CheckinView;
import javax.inject.Inject;

public class CheckinPresenter implements Presenter {

    private final GetCheckinStatusInteractor getCheckinStatusInteractor;
    private final PerformCheckinInteractor performCheckinInteractor;
    private final PerformCheckoutInteractor performCheckoutInteractor;

    private CheckinView checkinView;
    protected Boolean checkedInEvent;
    protected boolean showingCheckinButton;
    private String idEvent;

    @Inject public CheckinPresenter(GetCheckinStatusInteractor getCheckinStatusInteractor,
      PerformCheckinInteractor performCheckinInteractor, PerformCheckoutInteractor performCheckoutInteractor) {
        this.getCheckinStatusInteractor = getCheckinStatusInteractor;
        this.performCheckinInteractor = performCheckinInteractor;
        this.performCheckoutInteractor = performCheckoutInteractor;
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
            checkinView.showTextCheckOut();
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
                checkedInEvent = true;
                checkinView.hideCheckinLoading();
                hideViewCheckinButton();
                checkinView.showCheckedIn();
                checkinView.showTextCheckOut();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO veremos
            }
        });
    }

    protected void checkOut() {
        checkinView.showCheckinLoading();
        performCheckoutInteractor.performCheckout(idEvent, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                checkedInEvent = false;
                hideViewCheckinButton();
                checkinView.hideCheckinLoading();
                checkinView.showTextCheckIn();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                // TODO veremos tambien
            }
        });
    }

    private void showViewCheckinButton() {
        showingCheckinButton = true;
        checkinView.showCheckinButton();
    }

    private void hideViewCheckinButton() {
        showingCheckinButton = false;
        checkinView.hideCheckinButton();
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
