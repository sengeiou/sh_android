package com.shootr.android.ui.presenter;

import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.data.prefs.CheckinUserNotification;
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
    protected Boolean checkedInEvent;
    protected boolean showingCheckinButton;
    private String idEvent;

    private final BooleanPreference checkinUserNotificationPreference;

    @Inject public CheckinPresenter(GetCheckinStatusInteractor getCheckinStatusInteractor,
      PerformCheckinInteractor performCheckinInteractor,
      @CheckinUserNotification BooleanPreference checkinUserNotificationPreference) {
        this.getCheckinStatusInteractor = getCheckinStatusInteractor;
        this.performCheckinInteractor = performCheckinInteractor;
        this.checkinUserNotificationPreference = checkinUserNotificationPreference;
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
        }
    }

    public void checkinClick() {
        checkinView.showCheckinNotification(checkinUserNotificationPreference.get());
    }

    public void checkIn() {
        hideViewCheckinButton();
        performCheckinInteractor.performCheckin(idEvent, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                checkedInEvent = true;
                checkinView.showCheckedInNotification();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                checkinView.enableCheckinButton();
                checkinView.showErrorWhileCheckingNotification();
                checkinView.showCheckinButton();
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

    public void saveUserNotificationPreferences() {
        checkinUserNotificationPreference.set(false);
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
