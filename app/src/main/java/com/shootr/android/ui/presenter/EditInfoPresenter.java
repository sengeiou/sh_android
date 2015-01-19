package com.shootr.android.ui.presenter;

import android.os.Bundle;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.data.prefs.WatchingNotificationsAlertShown;
import com.shootr.android.task.jobs.info.SetWatchingInfoOfflineJob;
import com.shootr.android.task.jobs.info.SetWatchingInfoOnlineJob;
import com.shootr.android.ui.views.EditInfoView;
import dagger.ObjectGraph;
import javax.inject.Inject;

public class EditInfoPresenter {

    private final JobManager jobManager;

    private final BooleanPreference watchingNotificationsAlertShown;

    private EditInfoView editInfoView;
    private ObjectGraph objectGraph;

    private EditInfoModel editInfoModel;
    private boolean newWatchingStatus;

    @Inject public EditInfoPresenter(JobManager jobManager, @WatchingNotificationsAlertShown BooleanPreference watchingNotificationsAlertShown) {
        this.jobManager = jobManager;
        this.watchingNotificationsAlertShown = watchingNotificationsAlertShown;
    }

    public void initialize(EditInfoView editInfoView, EditInfoModel editInfoModel, ObjectGraph objectGraph) {
        this.editInfoView = editInfoView;
        this.editInfoModel = editInfoModel;
        newWatchingStatus = editInfoModel.watching;
        this.objectGraph = objectGraph;
        this.updateViewWithInfo();
    }

    public void watchingStatusChanged() {
        newWatchingStatus = editInfoView.getWatchingStatus();
        this.updateSendButtonStatus();
        this.updatePlaceInputStatus();
    }

    private void updatePlaceInputStatus() {
        if (!newWatchingStatus) {
            this.editInfoView.disablePlaceText();
        } else {
            this.editInfoView.enablePlaceText();
            this.editInfoView.setFocusOnPlace();
        }
    }

    public void placeTextChanged() {
        this.updateSendButtonStatus();
    }

    private void updateSendButtonStatus() {
        editInfoView.setSendButonEnabled(hasChangedInfo());
    }

    public void sendNewStatus() {
        String placeText = getPlaceText();
        if (placeText!= null && placeText.equalsIgnoreCase("not watching")) {
            editInfoView.alertPlaceNotWatchingNotAllow();
            return;
        }
        executeEditJobs(placeText);
        showNotificationsAlertFirstTime();
        closeScreen();
    }

    private String getPlaceText() {
        String placeText = this.editInfoView.getPlaceText();
        if (placeText != null) {
            placeText = filterPlaceText(placeText);
        }
        return placeText;
    }

    public String filterPlaceText(String placeText) {
        //TODO can't be more than [60] characters (business logic)
        placeText = placeText.trim();
        placeText = placeText.replace("\n", "");
        if (placeText.isEmpty()) {
            placeText = null;
        }
        return placeText;
    }

    private Long getWatchingStatusFromBoolean(boolean watching) {
        return watching ? 1L : 2L;
    }

    private void updateViewWithInfo() {
        this.editInfoView.setTitle(editInfoModel.eventTitle);
        this.editInfoView.setPlaceText(editInfoModel.place);
        this.editInfoView.setWatchingStatus(editInfoModel.watching);
        this.updatePlaceInputStatus();
    }

    private void closeScreen() {
        this.editInfoView.closeScreen();
    }

    private void showNotificationsAlertFirstTime() {
        boolean shouldShow = newWatchingStatus && !watchingNotificationsAlertShown.get();
        if (shouldShow) {
            showNotificationsAlert();
            watchingNotificationsAlertShown.set(true);
        }
    }

    private void showNotificationsAlert() {
        editInfoView.showNotificationsAlert();
    }

    private void executeEditJobs(String placeText) {
        SetWatchingInfoOfflineJob setWatchingInfoOfflineJob = objectGraph.get(SetWatchingInfoOfflineJob.class);
        setWatchingInfoOfflineJob.init(editInfoModel.idEvent, getWatchingStatusFromBoolean(newWatchingStatus),
          placeText);
        jobManager.addJobInBackground(setWatchingInfoOfflineJob);

        SetWatchingInfoOnlineJob setWatchingInfoOnlineJob = objectGraph.get(SetWatchingInfoOnlineJob.class);
        jobManager.addJobInBackground(setWatchingInfoOnlineJob);
    }

    private boolean hasChangedInfo() {
        boolean statusChanged = editInfoModel.watching != newWatchingStatus;
        boolean placeChanged = placeTextHasChanged();
        return statusChanged || placeChanged;
    }

    private boolean placeTextHasChanged() {
        String newPlaceText = getPlaceText();
        String currentPlaceText = editInfoModel.place;
        if (currentPlaceText == null) {
            return newPlaceText != null;
        } else {
            return !currentPlaceText.equals(newPlaceText);
        }
    }

    public static class EditInfoModel {

        private static final String KEY_ID_EVENT = "event";
        private static final String KEY_WATCHING_STATUS = "watching";
        private static final String KEY_EVENT_TITLE = "event_title";
        private static final String KEY_WATCHING_PLACE = "place";

        Long idEvent;
        String eventTitle;
        boolean watching;
        String place;

        public EditInfoModel() {
        }

        public EditInfoModel(Long idEvent, String eventTitle, boolean watchingStatus, String place) {
            this.idEvent = idEvent;
            this.eventTitle = eventTitle;
            this.watching = watchingStatus;
            this.place = place;
        }

        public static EditInfoModel fromBundle(Bundle bundle) {
            EditInfoModel model = new EditInfoModel();
            model.idEvent = bundle.getLong(KEY_ID_EVENT);
            model.watching = bundle.getBoolean(KEY_WATCHING_STATUS);
            model.eventTitle = bundle.getString(KEY_EVENT_TITLE);
            model.place = bundle.getString(KEY_WATCHING_PLACE);
            return model;
        }

        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putLong(KEY_ID_EVENT, idEvent);
            bundle.putBoolean(KEY_WATCHING_STATUS, watching);
            bundle.putString(KEY_EVENT_TITLE, eventTitle);
            bundle.putString(KEY_WATCHING_PLACE, place);
            return bundle;
        }
    }
}
