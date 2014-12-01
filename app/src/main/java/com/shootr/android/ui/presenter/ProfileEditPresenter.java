package com.shootr.android.ui.presenter;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.data.SessionManager;
import com.shootr.android.task.jobs.profile.UpdateUserProfileJob;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.validation.FieldValidationErrorEvent;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ProfileEditView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ProfileEditPresenter implements Presenter {

    private ProfileEditView profileEditView;
    private ObjectGraph objectGraph;

    private SessionManager sessionManager;
    private UserModelMapper userModelMapper;
    private Bus bus;
    private ErrorMessageFactory errorMessageFactory;
    private JobManager jobManager;

    private UserModel currentUserModel;

    @Inject public ProfileEditPresenter(SessionManager sessionManager, UserModelMapper userModelMapper, Bus bus,
      ErrorMessageFactory errorMessageFactory, JobManager jobManager) {
        this.sessionManager = sessionManager;
        this.userModelMapper = userModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.jobManager = jobManager;
    }

    public void initialize(ProfileEditView profileEditView, ObjectGraph objectGraph) {
        this.profileEditView = profileEditView;
        this.objectGraph = objectGraph;
        this.fillCurrentUserData();
        this.profileEditView.hideKeyboard();
    }

    private void fillCurrentUserData() {
        currentUserModel = userModelMapper.toUserModel(sessionManager.getCurrentUser(), null, true);
        this.profileEditView.renderUserInfo(currentUserModel);
    }

    public void discard() {
        //TODO confirmation alert()
        profileEditView.closeScreen();
    }

    public void done() {
        UserModel updatedUserModel = this.getUpadtedUserData();
        this.saveUpdatedProfile(updatedUserModel);
    }

    private UserModel getUpadtedUserData() {
        UserModel updatedUserModel = currentUserModel.clone();
        updatedUserModel.setUsername(cleanUsername());
        updatedUserModel.setName(cleanName());
        return updatedUserModel;
    }

    private String cleanUsername() {
        String username = profileEditView.getUsername();
        return trimAndNullWhenEmpty(username);
    }

    private String cleanName() {
        String name = profileEditView.getName();
        return trimAndNullWhenEmpty(name);
    }

    private String trimAndNullWhenEmpty(String text) {
        if (text != null) {
            text = text.trim();
            if (text.isEmpty()) {
                text = null;
            }
        }
        return text;
    }

    private void saveUpdatedProfile(UserModel updatedUserModel) {
        UpdateUserProfileJob job = objectGraph.get(UpdateUserProfileJob.class);
        job.init(updatedUserModel);
        jobManager.addJobInBackground(job);
    }

    //TODO @Subscribe
    public void onUserProfileUpdated() {
        profileEditView.showUpdatedSuccessfulAlert();
        profileEditView.closeScreen();
    }

    @Subscribe
    public void onValidationError(FieldValidationErrorEvent event) {
        List<FieldValidationError> fieldValidationErrors = event.getFieldValidationErrors();
        for (FieldValidationError validationError : fieldValidationErrors) {
            this.showValidationError(validationError);
        }
    }

    private void showValidationError(FieldValidationError validationError) {
        switch (validationError.getField()) {
            case FieldValidationError.FIELD_USERNAME:
                this.showUsernameValidationError(validationError.getErrorCode());
                break;
            case FieldValidationError.FIELD_NAME:
                this.showNameValidationError(validationError.getErrorCode());
        }
    }

    private void showUsernameValidationError(String errorCode) {
        String messageForCode = errorMessageFactory.getMessageForCode(errorCode);
        profileEditView.showUsernameValidationError(messageForCode);
    }

    private void showNameValidationError(String errorCode) {
        String messageForCode = errorMessageFactory.getMessageForCode(errorCode);
        profileEditView.showNameValidationError(messageForCode);
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
