package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.DomainValidationException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.user.GetUserByIdInteractor;
import com.shootr.mobile.domain.interactor.user.UpdateUserProfileInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.task.validation.FieldValidationError;
import com.shootr.mobile.task.validation.profile.BioValidator;
import com.shootr.mobile.task.validation.profile.NameValidator;
import com.shootr.mobile.task.validation.profile.UsernameValidator;
import com.shootr.mobile.task.validation.profile.WebsiteValidator;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.ProfileEditView;
import com.shootr.mobile.util.ErrorMessageFactory;
import dagger.ObjectGraph;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ProfileEditPresenter implements Presenter {

    private static final String PROTOCOL_PATTERN = "\"^https?:\\\\/\\\\/\"";
    private ProfileEditView profileEditView;

    private final SessionRepository sessionRepository;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final GetUserByIdInteractor getUserByIdInteractor;
    private final UpdateUserProfileInteractor updateUserProfileInteractor;

    private UserModel currentUserModel;
    private boolean hasBeenPaused = false;
    private boolean discardConfirmEmailAlert = false;

    private final List<FieldValidationError> fieldValidationErrors;

    @Inject public ProfileEditPresenter(SessionRepository sessionRepository, UserModelMapper userModelMapper,
      ErrorMessageFactory errorMessageFactory,
      GetUserByIdInteractor getUserByIdInteractor, UpdateUserProfileInteractor updateUserProfileInteractor) {
        this.sessionRepository = sessionRepository;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
        this.getUserByIdInteractor = getUserByIdInteractor;
        this.updateUserProfileInteractor = updateUserProfileInteractor;
        this.fieldValidationErrors = new ArrayList<>();
    }

    public void initialize(ProfileEditView profileEditView) {
        this.profileEditView = profileEditView;
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
        localValidation(updatedUserModel);

        if (hasLocalErrors()) {
            postValidationErrors();
        } else {
            User user = updateUserWithValues(updatedUserModel);
            updateUserProfileInteractor.updateProfile(user, new Interactor.CompletedCallback() {
                @Override public void onCompleted() {
                    profileEditView.showUpdatedSuccessfulAlert();
                    profileEditView.closeScreen();
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    updateProfileError(error);
                }
            });
        }
    }

    private void updateProfileError(ShootrException error) {
        if (error instanceof DomainValidationException) {
            DomainValidationException validationException = (DomainValidationException) error;
            List<com.shootr.mobile.domain.validation.FieldValidationError> errors = validationException.getErrors();
            showValidationErrors(errors);
        } else if (error instanceof ServerCommunicationException) {
            this.profileEditView.alertComunicationError();
        } else {
            Timber.e(error, "Unknown error creating account.");
            profileEditView.showError(errorMessageFactory.getUnknownErrorMessage());
        }
    }

    private void showValidationErrors(List<com.shootr.mobile.domain.validation.FieldValidationError> errors) {
        for (com.shootr.mobile.domain.validation.FieldValidationError validationError : errors) {
            String errorCode = errorMessageFactory.getMessageForCode(validationError.getErrorCode());
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

    @Override public void resume() {
        if (hasBeenPaused) {
            fillCurrentUserData();
        }
    }

    @Override public void pause() {
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
