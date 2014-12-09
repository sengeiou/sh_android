package com.shootr.android.ui.presenter;

import android.widget.Toast;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.ShootrApplication;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.shots.PostNewShotResultEvent;
import com.shootr.android.task.jobs.shots.PostNewShotJob;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.validation.FieldValidationErrorEvent;
import com.shootr.android.ui.views.PostNewShotView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class PostNewShotPresenter implements Presenter {

    private static final int MAX_LENGTH = 140;

    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final JobManager jobManager;

    private PostNewShotView postNewShotView;
    private ObjectGraph objectGraph;

    @Inject public PostNewShotPresenter(Bus bus, ErrorMessageFactory errorMessageFactory, JobManager jobManager) {
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.jobManager = jobManager;
    }

    public void initialize(PostNewShotView postNewShotView, ObjectGraph objectGraph) {
        this.postNewShotView = postNewShotView;
        this.objectGraph = objectGraph;
    }

    public void textChanged(String currentText) {
        String filteredText = filterText(currentText);
        updateCharCounter(filteredText);
        updateSendButonEnabled(filteredText);
    }

    public void sendShot(String text) {
        postNewShotView.hideKeyboard();
        String commentFiltered = filterText(text);

        if (isNotEmptyAndLessThanMaxLenght(commentFiltered)) {
            startSendingShot(commentFiltered);
        } else {
            Timber.w("Tried to send shot empty or too big: %s", commentFiltered);
        }
    }

    private void startSendingShot(String comment) {
        postNewShotView.showLoading();
        postNewShotView.disableSendButton();

        PostNewShotJob job = objectGraph.get(PostNewShotJob.class);
        job.init(comment);
        jobManager.addJobInBackground(job);
    }

    private void updateCharCounter(String filteredText) {
        int remainingLength = MAX_LENGTH - filteredText.length();
        postNewShotView.setRemainingCharactersCount(remainingLength);

        boolean isValidShot = remainingLength > 0;
        if (isValidShot) {
            postNewShotView.setRemainingCharactersColorValid();
        } else {
            postNewShotView.setRemainingCharactersColorInvalid();
        }
    }

    private void updateSendButonEnabled(String filteredText) {
        if (isNotEmptyAndLessThanMaxLenght(filteredText)) {
            postNewShotView.enableSendButton();
        } else {
            postNewShotView.disableSendButton();
        }
    }

    private boolean isNotEmptyAndLessThanMaxLenght(String text) {
        return text.length() > 0 && text.length() <= MAX_LENGTH;
    }

    private String filterText(String originalText) {
        String trimmed = originalText.trim();
        while (trimmed.contains("\n\n\n")) {
            trimmed = trimmed.replace("\n\n\n", "\n\n");
        }
        return trimmed;
    }

    @Subscribe
    public void onShotSent(PostNewShotResultEvent event) {
        postNewShotView.setResultOk();
        postNewShotView.closeScreen();
    }

    @Subscribe
    public void onValidationErrors(FieldValidationErrorEvent event) {
        postNewShotView.hideLoading();
        postNewShotView.enableSendButton();

        List<FieldValidationError> fieldValidationErrors = event.getFieldValidationErrors();
        FieldValidationError firstError = fieldValidationErrors.get(0);
        String errorMessage = errorMessageFactory.getMessageForCode(firstError.getErrorCode());
        postNewShotView.showError(errorMessage);
    }

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event) {
        postNewShotView.hideLoading();
        postNewShotView.enableSendButton();
        postNewShotView.showError(errorMessageFactory.getCommunicationErrorMessage());
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        postNewShotView.hideLoading();
        postNewShotView.enableSendButton();
        postNewShotView.showError(errorMessageFactory.getConnectionNotAvailableMessage());
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
