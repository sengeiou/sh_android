package com.shootr.android.ui.presenter;

import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.data.prefs.AskCheckinConfirmation;
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
    protected boolean showingCheckinButton;
    private String idEvent;

    private final BooleanPreference askCheckinConfirmation;

    @Inject public CheckinPresenter(GetCheckinStatusInteractor getCheckinStatusInteractor,
      PerformCheckinInteractor performCheckinInteractor,
      @AskCheckinConfirmation BooleanPreference askCheckinConfirmation) {
        this.getCheckinStatusInteractor = getCheckinStatusInteractor;
        this.performCheckinInteractor = performCheckinInteractor;
        this.askCheckinConfirmation = askCheckinConfirmation;
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
        this.loadCheckinStatus();
    }

    private void loadCheckinStatus() {
        getCheckinStatusInteractor.loadCheckinStatus(idEvent, new Interactor.Callback<Boolean>() {
            @Override public void onLoaded(Boolean checkedIn) {
                if(!checkedIn){
                    showViewCheckinButton();
                }
            }
        });
    }

    public void checkIn() {
        if(shouldAskForConfirmation()){
            checkinView.showCheckinConfirmation();
        } else {
            confirmCheckinDontShowAgain();
        }
    }

    public void confirmCheckin() {
        hideViewCheckinButton();
        performCheckinInteractor.performCheckin(idEvent, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                checkinView.showCheckinDone();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                checkinView.enableCheckinButton();
                checkinView.showCheckinError();
                checkinView.showCheckinButton();
            }
        });
    }

    public void confirmCheckinDontShowAgain() {
        neverShowConfirmationToUserAgain();
        confirmCheckin();
    }

    private void showViewCheckinButton() {
        showingCheckinButton = true;
        checkinView.showCheckinButton();
    }

    private void hideViewCheckinButton() {
        showingCheckinButton = false;
        checkinView.hideCheckinButton();
    }

    private boolean shouldAskForConfirmation() {
        return askCheckinConfirmation.get();
    }

    private void neverShowConfirmationToUserAgain() {
        askCheckinConfirmation.set(false);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
