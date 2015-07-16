package com.shootr.android.ui.presenter;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.UpdateUserInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.profile.UpdateUserProfileEvent;
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

public class ProfileEditPresenter implements Presenter {

    private static final String PROTOCOL_PATTERN = "\"^https?:\\\\/\\\\/\"";
    public static final String EMAIL_HAS_NOT_BEEN_CONFIRMED_YET = "Email has not been confirmed yet";
    private ProfileEditView profileEditView;
    private ObjectGraph objectGraph;

    private final SessionRepository sessionRepository;
    private final UserModelMapper userModelMapper;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final JobManager jobManager;
    private final UpdateUserInteractor updateUserInteractor;

    private UserModel currentUserModel;

    @Inject public ProfileEditPresenter(SessionRepository sessionRepository, UserModelMapper userModelMapper, @Main Bus bus,
      ErrorMessageFactory errorMessageFactory, JobManager jobManager, UpdateUserInteractor updateUserInteractor) {
        this.sessionRepository = sessionRepository;
        this.userModelMapper = userModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.jobManager = jobManager;
        this.updateUserInteractor = updateUserInteractor;
    }

    public void initialize(ProfileEditView profileEditView, ObjectGraph objectGraph) {
        this.profileEditView = profileEditView;
        this.objectGraph = objectGraph;
        this.fillCurrentUserData(false);
        this.profileEditView.hideKeyboard();
    }

    private void fillCurrentUserData(boolean resume) {
        updateUser(resume);
        currentUserModel = userModelMapper.transform(sessionRepository.getCurrentUser());
        this.profileEditView.renderUserInfo(currentUserModel);
        if(!currentUserModel.getEmailConfirmed() && !resume) {
            profileEditView.showEmailNotConfirmedError(EMAIL_HAS_NOT_BEEN_CONFIRMED_YET);
        }
    }

    private void updateUser(boolean resume) {
        if(!resume) {
            updateUserInteractor.updateCurrentUser(new Interactor.CompletedCallback() {
                @Override public void onCompleted() {
                    /* no-op */
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    profileEditView.alertComunicationError();
                }
            });
        }
    }

    public void discard() {
        if (hasChangedData()) {
            profileEditView.showDiscardConfirmation();
        } else {
            profileEditView.closeScreen();
        }
    }

    public void confirmDiscard() {
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
        updatedUserModel.setBio(cleanBio());
        updatedUserModel.setWebsite(cleanWebsite());
        return updatedUserModel;
    }


    private boolean hasChangedData() {
        UserModel updatedUserData = getUpadtedUserData();
        boolean changedUsername = !currentUserModel.getUsername().equals(updatedUserData.getUsername());
        boolean changedName = currentUserModel.getName() == null ? updatedUserData.getName() != null : !currentUserModel.getName().equals(updatedUserData.getName());
        boolean changedBio = currentUserModel.getBio() == null ? updatedUserData.getBio() != null : !currentUserModel.getBio().equals(updatedUserData.getBio());
        boolean changedWebsite = currentUserModel.getWebsite() == null ? updatedUserData.getWebsite() != null : !currentUserModel.getWebsite().equals(updatedUserData.getWebsite());
        return changedName || changedUsername || changedWebsite || changedBio;
    }

    private String cleanUsername() {
        String username = profileEditView.getUsername();
        return trimAndNullWhenEmpty(username);
    }

    private String cleanName() {
        String name = profileEditView.getName();
        return trimAndNullWhenEmpty(name);
    }

    private String cleanBio() {
        String bio = profileEditView.getBio();
        bio = removeWhiteLines(bio);
        return trimAndNullWhenEmpty(bio);
    }

    private String cleanWebsite() {
        String website = profileEditView.getWebsite();
        website = removeProtocolFromUrl(website);
        return trimAndNullWhenEmpty(website);
    }

    private String removeWhiteLines(String multilineText) {
        String textWithoutWhiteLines = multilineText;
        while (textWithoutWhiteLines.contains("\n\n")) {
            textWithoutWhiteLines = textWithoutWhiteLines.replace("\n\n", "\n");
        }
        return textWithoutWhiteLines;
    }

    private String trimAndNullWhenEmpty(String text) {
        String trimmedText = text;
        if (trimmedText != null) {
            trimmedText = trimmedText.trim();
            if (trimmedText.isEmpty()) {
                return null;
            }
        }
        return trimmedText;
    }

    private String removeProtocolFromUrl(String url) {
        return url.replaceAll(PROTOCOL_PATTERN, "");
    }

    private void saveUpdatedProfile(UserModel updatedUserModel) {
        UpdateUserProfileJob job = objectGraph.get(UpdateUserProfileJob.class);
        job.init(updatedUserModel);
        jobManager.addJobInBackground(job);
        this.showLoading();
    }

    @Subscribe
    public void onUserProfileUpdated(UpdateUserProfileEvent event) {
        profileEditView.showUpdatedSuccessfulAlert();
        profileEditView.closeScreen();
    }

    @Subscribe
    public void onValidationErrors(FieldValidationErrorEvent event) {
        this.hideLoading();
        List<FieldValidationError> fieldValidationErrors = event.getFieldValidationErrors();
        for (FieldValidationError validationError : fieldValidationErrors) {
            this.showValidationError(validationError);
        }
    }

    private void showValidationError(FieldValidationError validationError) {
        String errorCode = validationError.getErrorCode();
        switch (validationError.getField()) {
            case FieldValidationError.FIELD_USERNAME:
                this.showUsernameValidationError(errorCode);
                break;
            case FieldValidationError.FIELD_NAME:
                this.showNameValidationError(errorCode);
                break;
            case FieldValidationError.FIELD_BIO:
                this.showBioValidationError(errorCode);
                break;
            case FieldValidationError.FIELD_WEBSITE:
                this.showWebsiteValidationError(errorCode);
                break;
            default:
                break;
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

    private void showWebsiteValidationError(String errorCode) {
        String messageForCode = errorMessageFactory.getMessageForCode(errorCode);
        profileEditView.showWebsiteValidationError(messageForCode);
    }

    private void showBioValidationError(String errorCode) {
        String messageForCode = errorMessageFactory.getMessageForCode(errorCode);
        profileEditView.showBioValidationError(messageForCode);
    }

    private void showLoading() {
        profileEditView.showLoadingIndicator();
    }

    private void hideLoading() {
        profileEditView.hideLoadingIndicator();
    }
    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event) {
        this.hideLoading();
        this.profileEditView.alertComunicationError();
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        this.hideLoading();
        this.profileEditView.alertConnectionNotAvailable();
    }

    @Override public void resume() {
        bus.register(this);
        fillCurrentUserData(true);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
