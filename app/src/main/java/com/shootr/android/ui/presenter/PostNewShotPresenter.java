package com.shootr.android.ui.presenter;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.profile.UploadShotImageEvent;
import com.shootr.android.task.events.shots.PostNewShotResultEvent;
import com.shootr.android.task.jobs.shots.PostNewShotJob;
import com.shootr.android.task.jobs.shots.UploadShotImageJob;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.validation.FieldValidationErrorEvent;
import com.shootr.android.ui.views.PostNewShotView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import java.io.File;
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
    private File selectedImageFile;
    private String shotCommentToSend;
    private String uploadedImageUrl;
    private String currentTextWritten;

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
        currentTextWritten = filterText(currentText);
        updateCharCounter(currentTextWritten);
        updateSendButonEnabled(currentTextWritten);
    }

    public void choosePhotoFromGallery() {
        postNewShotView.choosePhotoFromGallery();
    }

    public void takePhotoFromCamera() {
        postNewShotView.takePhotoFromCamera();
    }

    public void selectImage(File selectedImageFile) {
        if (selectedImageFile != null && selectedImageFile.exists()) {
            postNewShotView.showImagePreview(selectedImageFile);
            this.selectedImageFile = selectedImageFile;
            updateSendButonEnabled(currentTextWritten);
        } else {
            Timber.w("Tried to set invalid file as image: %", selectedImageFile);
        }
    }

    public void removeImage() {
        selectedImageFile = null;
        postNewShotView.hideImagePreview();
        updateSendButonEnabled(currentTextWritten);
    }

    public void sendShot(String text) {
        postNewShotView.hideKeyboard();
        shotCommentToSend = filterText(text);

        if (canSendShot(shotCommentToSend)) {
            if (selectedImageFile != null) {
                uploadImageAndSendShot();
            } else {
                startSendingShot();
            }
        } else {
            Timber.w("Tried to send shot empty or too big: %s", shotCommentToSend);
        }
    }

    private boolean canSendShot(String filteredText) {
        return (hasImage() && isLessThanMaxLength(filteredText)) || isNotEmptyAndLessThanMaxLenght(filteredText);
    }

    private boolean hasImage() {
        return selectedImageFile != null;
    }

    private void uploadImageAndSendShot() {
        UploadShotImageJob uploadJob = objectGraph.get(UploadShotImageJob.class);
        uploadJob.init(selectedImageFile);
        jobManager.addJob(uploadJob);
    }

    @Subscribe
    public void onImageUploaded(UploadShotImageEvent event) {
        uploadedImageUrl = event.getResult();
        startSendingShot();
    }

    private void startSendingShot() {
        postNewShotView.showLoading();
        postNewShotView.disableSendButton();

        PostNewShotJob job = objectGraph.get(PostNewShotJob.class);
        job.init(shotCommentToSend, uploadedImageUrl);
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
        if (canSendShot(filteredText)) {
            postNewShotView.enableSendButton();
        } else {
            postNewShotView.disableSendButton();
        }
    }

    private boolean isNotEmptyAndLessThanMaxLenght(String text) {
        return text.length() > 0 && isLessThanMaxLength(text);
    }

    private boolean isLessThanMaxLength(String text) {
        return text.length() <= MAX_LENGTH;
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
