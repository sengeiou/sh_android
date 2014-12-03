package com.shootr.android.ui.presenter;

import android.os.Bundle;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.task.jobs.info.DeleteMatchOfflineJob;
import com.shootr.android.task.jobs.info.DeleteMatchOnlineJob;
import com.shootr.android.task.jobs.info.SetWatchingInfoOfflineJob;
import com.shootr.android.task.jobs.info.SetWatchingInfoOnlineJob;
import com.shootr.android.ui.views.EditInfoView;
import dagger.ObjectGraph;
import javax.inject.Inject;

public class EditInfoPresenter {

    private EditInfoView editInfoView;

    private EditInfoModel editInfoModel;
    private ObjectGraph objectGraph;
    private JobManager jobManager;

    private boolean newWatchingStatus;

    @Inject public EditInfoPresenter(JobManager jobManager) {
        this.jobManager = jobManager;
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
        this.editInfoView.setTitle(editInfoModel.matchTitle);
        this.editInfoView.setPlaceText(editInfoModel.place);
        this.editInfoView.setWatchingStatus(editInfoModel.watching);
        this.updatePlaceInputStatus();
    }

    private void closeScreen() {
        this.editInfoView.closeScreen();
    }

    private void executeEditJobs(String placeText) {
        SetWatchingInfoOfflineJob setWatchingInfoOfflineJob = objectGraph.get(SetWatchingInfoOfflineJob.class);
        setWatchingInfoOfflineJob.init(editInfoModel.idMatch, getWatchingStatusFromBoolean(newWatchingStatus),
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

    public void deleteMatch() {
        showDeleteMatchConfirmation();
    }

    public void showDeleteMatchConfirmation() {
        String confirmationTitle;
        String confirmationMessage;
        if (editInfoModel.watching) {
            confirmationTitle = "Remove match?";
            confirmationMessage =
              String.format("This will shoot you are not watching %s.", editInfoModel.matchTitle);
        } else {
            confirmationTitle = null;
            confirmationMessage = "Remove match?";
        }
        this.editInfoView.showDeleteMatchConfirmation(confirmationTitle, confirmationMessage);
    }

    public void confirmDeleteMatch() {
        DeleteMatchOfflineJob deleteMatchOfflineJob = objectGraph.get(DeleteMatchOfflineJob.class);
        deleteMatchOfflineJob.init(editInfoModel.idMatch);
        jobManager.addJobInBackground(deleteMatchOfflineJob);

        DeleteMatchOnlineJob deleteMatchOnlineJob = objectGraph.get(DeleteMatchOnlineJob.class);
        jobManager.addJobInBackground(deleteMatchOnlineJob);

        closeScreen();
    }

    public static class EditInfoModel {

        private static final String KEY_ID_MATCH = "match";
        private static final String KEY_WATCHING_STATUS = "watching";
        private static final String KEY_MATCH_TITLE = "matc_title";
        private static final String KEY_WATCHING_PLACE = "place";

        Long idMatch;
        String matchTitle;
        boolean watching;
        String place;

        public EditInfoModel() {
        }

        public EditInfoModel(Long idMatch, String matchTitle, boolean watchingStatus, String place) {
            this.idMatch = idMatch;
            this.matchTitle = matchTitle;
            this.watching = watchingStatus;
            this.place = place;
        }

        public static EditInfoModel fromBundle(Bundle bundle) {
            EditInfoModel model = new EditInfoModel();
            model.idMatch = bundle.getLong(KEY_ID_MATCH);
            model.watching = bundle.getBoolean(KEY_WATCHING_STATUS);
            model.matchTitle = bundle.getString(KEY_MATCH_TITLE);
            model.place = bundle.getString(KEY_WATCHING_PLACE);
            return model;
        }

        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putLong(KEY_ID_MATCH, idMatch);
            bundle.putBoolean(KEY_WATCHING_STATUS, watching);
            bundle.putString(KEY_MATCH_TITLE, matchTitle);
            bundle.putString(KEY_WATCHING_PLACE, place);
            return bundle;
        }
    }
}
