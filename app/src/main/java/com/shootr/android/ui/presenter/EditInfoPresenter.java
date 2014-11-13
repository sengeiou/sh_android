package com.shootr.android.ui.presenter;

import android.os.Bundle;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.task.jobs.info.SetWatchingInfoOfflineJob;
import com.shootr.android.task.jobs.info.SetWatchingInfoOnlineJob;
import com.shootr.android.ui.views.EditInfoView;
import dagger.ObjectGraph;
import javax.inject.Inject;
import timber.log.Timber;

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
        this.objectGraph = objectGraph;
        this.updateViewWithInfo();
    }

    public void watchingStatusChanged(boolean watching) {
        newWatchingStatus = watching;
        editInfoView.setSendButonEnabled(hasChangedInfo(watching));
    }

    public void sendNewStatus() {
        executeJobs();
        closeScreen();
    }

    private Long getWatchingStatusFromBoolean(boolean watching) {
        return watching ? 1L : 2L;
    }

    private void updateViewWithInfo() {
        this.editInfoView.setTitle(editInfoModel.matchTitle);
        this.editInfoView.setWatchingStatus(editInfoModel.watching);
    }

    private void closeScreen() {
        this.editInfoView.closeScreen();
    }

    private void executeJobs() {
        Timber.d("Watching: " + newWatchingStatus);

        SetWatchingInfoOfflineJob setWatchingInfoOfflineJob = objectGraph.get(SetWatchingInfoOfflineJob.class);
        setWatchingInfoOfflineJob.init(editInfoModel.idMatch, getWatchingStatusFromBoolean(newWatchingStatus));
        jobManager.addJobInBackground(setWatchingInfoOfflineJob);

        SetWatchingInfoOnlineJob setWatchingInfoOnlineJob = objectGraph.get(SetWatchingInfoOnlineJob.class);
        jobManager.addJobInBackground(setWatchingInfoOnlineJob);
    }

    private boolean hasChangedInfo(boolean watching) {
        return editInfoModel.watching != watching;
    }

    public static class EditInfoModel {

        private static final String KEY_ID_MATCH = "match";
        private static final String KEY_WATCHING_STATUS = "watching";
        private static final String KEY_MATCH_TITLE = "matc_title";

        Long idMatch;
        String matchTitle;
        boolean watching;

        public EditInfoModel() {

        }

        public EditInfoModel(Long idMatch, String matchTitle, boolean watchingStatus) {
            this.idMatch = idMatch;
            this.matchTitle = matchTitle;
            this.watching = watchingStatus;
        }

        public static EditInfoModel fromBundle(Bundle bundle) {
            EditInfoModel model = new EditInfoModel();
            model.idMatch = bundle.getLong(KEY_ID_MATCH);
            model.watching = bundle.getBoolean(KEY_WATCHING_STATUS);
            model.matchTitle = bundle.getString(KEY_MATCH_TITLE);
            return model;
        }

        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putLong(KEY_ID_MATCH, idMatch);
            bundle.putBoolean(KEY_WATCHING_STATUS, watching);
            bundle.putString(KEY_MATCH_TITLE, matchTitle);
            return bundle;
        }
    }
}
