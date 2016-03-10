package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.user.GetUserByIdInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.task.events.CommunicationErrorEvent;
import com.shootr.mobile.task.events.ConnectionNotAvailableEvent;
import com.shootr.mobile.task.events.profile.UpdateUserProfileEvent;
import com.shootr.mobile.task.validation.FieldValidationError;
import com.shootr.mobile.task.validation.FieldValidationErrorEvent;
import com.shootr.mobile.task.validation.profile.BioValidator;
import com.shootr.mobile.task.validation.profile.NameValidator;
import com.shootr.mobile.task.validation.profile.UsernameValidator;
import com.shootr.mobile.task.validation.profile.WebsiteValidator;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.ProfileEditView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ProfileEditPresenter implements Presenter {

    private static final String PROTOCOL_PATTERN = "\"^https?:\\\\/\\\\/\"";
    private ProfileEditView profileEditView;
    private ObjectGraph objectGraph;

    private final SessionRepository sessionRepository;
    private final UserModelMapper userModelMapper;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final InteractorHandler interactorHandler;
    private final GetUserByIdInteractor getUserByIdInteractor;

    private UserModel currentUserModel;
    private boolean hasBeenPaused = false;
    private boolean discardConfirmEmailAlert = false;

    private final List<FieldValidationError> fieldValidationErrors;

    @Inject
    public ProfileEditPresenter(SessionRepository sessionRepository, UserModelMapper userModelMapper, @Main Bus bus,
      ErrorMessageFactory errorMessageFactory, InteractorHandler interactorHandler,
      GetUserByIdInteractor getUserByIdInteractor) {
        this.sessionRepository = sessionRepository;
        this.userModelMapper = userModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.interactorHandler = interactorHandler;
        this.getUserByIdInteractor = getUserByIdInteractor;
        this.fieldValidationErrors = new ArrayList<>();
    }

    public void initialize(ProfileEditView profileEditView, ObjectGraph objectGraph) {
        this.profileEditView = profileEditView;
        this.objectGraph = objectGraph;
        this.fillCurrentUserData();
        this.profileEditView.hideKeyboard();
    }

    private void fillCurrentUserData() {
        getUserByIdInteractor.loadUserById(sessionRepository.getCurrentUserId(), new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
                loadProfileData();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                profileEditView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    private void loadProfileData() {
        getUserByIdInteractor.loadUserById(sessionRepository.getCurrentUserId(), new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
                currentUserModel = userModelMapper.transform(sessionRepository.getCurrentUser());
                profileEditView.renderUserInfo(currentUserModel);
                if (!currentUserModel.isEmailConfirmed() && !discardConfirmEmailAlert) {
                    profileEditView.showEmailNotConfirmedError();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                profileEditView.alertComunicationError();
            }
        });
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
        UserModel updatedUserModel = this.getUpdatedUserData();
        this.saveUpdatedProfile(updatedUserModel);
    }

    private UserModel getUpdatedUserData() {
        UserModel updatedUserModel = currentUserModel.clone();
        updatedUserModel.setUsername(cleanUsername());
        updatedUserModel.setName(cleanName());
        updatedUserModel.setBio(cleanBio());
        updatedUserModel.setWebsite(cleanWebsite());
        return updatedUserModel;
    }

    private boolean hasChangedData() {
        UserModel updatedUserData = getUpdatedUserData();
        boolean changedUsername = !currentUserModel.getUsername().equals(updatedUserData.getUsername());
        boolean changedName = currentUserModel.getName() == null ? updatedUserData.getName() != null
          : !currentUserModel.getName().equals(updatedUserData.getName());
        boolean changedBio = currentUserModel.getBio() == null ? updatedUserData.getBio() != null
          : !currentUserModel.getBio().equals(updatedUserData.getBio());
        boolean changedWebsite = currentUserModel.getWebsite() == null ? updatedUserData.getWebsite() != null
          : !currentUserModel.getWebsite().equals(updatedUserData.getWebsite());
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
                trimmedText = null;
            }
        }
        return trimmedText;
    }

    private String removeProtocolFromUrl(String url) {
        return url.replaceAll(PROTOCOL_PATTERN, "");
    }

    private void saveUpdatedProfile(UserModel updatedUserModel) {
        //TODO: use interactor, create a User with userModel's attributes and do local validation before
        // sending to the interactorS, saying?
        localValidation(updatedUserModel);

        if (hasLocalErrors()) {
            postValidationErrors();
            return;
        }
        User user = updateUserWithValues(updatedUserModel);
    }

    private User updateUserWithValues(UserModel updatedUserModel) {
        User user = new User();
        user.setUsername(updatedUserModel.getUsername());
        user.setName(updatedUserModel.getName());
        user.setBio(updatedUserModel.getBio());
        user.setWebsite(updatedUserModel.getWebsite());
        user.setEmail(updatedUserModel.getEmail());
        user.setEmailConfirmed(updatedUserModel.isEmailConfirmed());
        return user;
    }

    @Subscribe public void onUserProfileUpdated(UpdateUserProfileEvent event) {
        profileEditView.showUpdatedSuccessfulAlert();
        profileEditView.closeScreen();
    }

    @Subscribe public void onValidationErrors(FieldValidationErrorEvent event) {
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

    @Subscribe public void onCommunicationError(CommunicationErrorEvent event) {
        this.hideLoading();
        this.profileEditView.alertComunicationError();
    }

    @Subscribe public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        this.hideLoading();
        this.profileEditView.alertConnectionNotAvailable();
    }

    @Override public void resume() {
        bus.register(this);
        if (hasBeenPaused) {
            fillCurrentUserData();
        }
    }

    @Override public void pause() {
        bus.unregister(this);
        hasBeenPaused = true;
    }

    public void editEmail() {
        if (!currentUserModel.isEmailConfirmed()) {
            profileEditView.hideEmailNotConfirmedError();
        }
        discardConfirmEmailAlert = true;
        profileEditView.navigateToEditEmail();
    }

    private void localValidation(UserModel user) {
        validateUsername(user);
        validateName(user);
        validateWebsite(user);
        validateBio(user);
    }

    private void validateWebsite(UserModel user) {
        addErrorsIfAny(new WebsiteValidator(user).validate());
    }

    private void validateUsername(UserModel user) {
        addErrorsIfAny(new UsernameValidator(user).validate());
    }

    private void validateName(UserModel user) {
        addErrorsIfAny(new NameValidator(user).validate());
    }

    private void validateBio(UserModel user) {
        addErrorsIfAny(new BioValidator(user).validate());
    }

    private void addErrorsIfAny(List<FieldValidationError> validationResult) {
        if (validationResult != null && !validationResult.isEmpty()) {
            fieldValidationErrors.addAll(validationResult);
        }
    }

    private boolean hasLocalErrors() {
        return !fieldValidationErrors.isEmpty();
    }

    private void postValidationErrors() {
        for (FieldValidationError fieldValidationError : fieldValidationErrors) {
            showValidationError(fieldValidationError);
        }
        fieldValidationErrors.clear();
    }
}
