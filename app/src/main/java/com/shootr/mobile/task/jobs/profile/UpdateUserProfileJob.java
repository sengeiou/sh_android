package com.shootr.mobile.task.jobs.profile;

import android.app.Application;
import com.shootr.mobile.data.api.entity.mapper.UserApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.UserApiService;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.UserEntityMapper;
import com.shootr.mobile.db.manager.UserManager;
import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.task.events.profile.UpdateUserProfileEvent;
import com.shootr.mobile.task.jobs.ShootrBaseJob;
import com.shootr.mobile.task.validation.FieldValidationError;
import com.shootr.mobile.task.validation.FieldValidationErrorEvent;
import com.shootr.mobile.task.validation.profile.BioValidator;
import com.shootr.mobile.task.validation.profile.NameValidator;
import com.shootr.mobile.task.validation.profile.UsernameValidator;
import com.shootr.mobile.task.validation.profile.WebsiteValidator;
import com.shootr.mobile.ui.model.UserModel;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UpdateUserProfileJob extends ShootrBaseJob<UpdateUserProfileEvent> {

    private static final int PRIORITY = 8;

    private SessionRepository sessionRepository;
    private UserManager userManager;
    private TimeUtils timeUtils;
    private UserEntityMapper userEntityMapper;
    private UserApiService userApiService;
    private UserApiEntityMapper userApiEntityMapper;

    private UserModel updatedUserModel;
    private final List<FieldValidationError> fieldValidationErrors;

    @Inject public UpdateUserProfileJob(Application application,
      @Main Bus bus, SessionRepository sessionRepository,
      UserManager userManager,
      TimeUtils timeUtils,
      UserEntityMapper userEntityMapper,
      UserApiService userApiService,
      UserApiEntityMapper userApiEntityMapper) {
        super(application, bus);
        this.userApiService = userApiService;
        this.sessionRepository = sessionRepository;
        this.userManager = userManager;
        this.timeUtils = timeUtils;
        this.userEntityMapper = userEntityMapper;
        this.fieldValidationErrors = new ArrayList<>();
        this.userApiEntityMapper = userApiEntityMapper;
    }

    //TODO domain logic should not know about view models
    public void init(UserModel updatedUserModel) {
        this.updatedUserModel = updatedUserModel;
    }

    @Override protected void run() throws IOException {
        localValidation();
        if (hasLocalErrors()) {
            postValidationErrors();
            return;
        }

        try {
            sendUpdatedProfileToServer();
            postSuccessfulEvent(new UpdateUserProfileEvent(null));
        } catch (ApiException apiError) {
            if (apiError.getErrorInfo() == ErrorInfo.UserNameAlreadyExistsException) {
                FieldValidationError fieldValidationError =
                  new FieldValidationError(ShootrError.ERROR_CODE_USERNAME_DUPLICATE,
                    FieldValidationError.FIELD_USERNAME);
                fieldValidationErrors.add(fieldValidationError);
                postValidationErrors();
            } else {
                throw new IOException(apiError);
            }
        }
    }

    private boolean hasLocalErrors() {
        return !fieldValidationErrors.isEmpty();
    }

    private void postValidationErrors() {
        postCustomEvent(new FieldValidationErrorEvent(fieldValidationErrors));
    }

    private UserEntity updateEntityWithValues(UserModel updatedUserModel) {
        UserEntity updatedUserEntity = userManager.getUserByIdUser(sessionRepository.getCurrentUserId());
        updatedUserEntity.setUserName(updatedUserModel.getUsername());
        updatedUserEntity.setName(updatedUserModel.getName());
        updatedUserEntity.setBio(updatedUserModel.getBio());
        updatedUserEntity.setWebsite(updatedUserModel.getWebsite());
        updatedUserEntity.setEmail(updatedUserModel.getEmail());
        updatedUserEntity.setEmailConfirmed(updatedUserModel.isEmailConfirmed() ? 1:0);
        return updatedUserEntity;
    }

    private void sendUpdatedProfileToServer() throws IOException, ApiException {
        UserEntity updatedUserEntity = updateEntityWithValues(updatedUserModel);

        updatedUserEntity.setModified(timeUtils.getCurrentDate());
        updatedUserEntity.setRevision(updatedUserEntity.getRevision() + 1);

        updatedUserEntity = userApiService.putUser(userApiEntityMapper.transform(updatedUserEntity));
        userManager.saveUser(updatedUserEntity);
        sessionRepository.setCurrentUser(userEntityMapper.transform(updatedUserEntity, updatedUserEntity.getIdUser()));
    }

    private void localValidation() {
        validateUsername();
        validateName();
        validateWebsite();
        validateBio();
    }

    private void validateWebsite() {
        addErrorsIfAny(new WebsiteValidator(updatedUserModel).validate());
    }

    private void validateUsername() {
        addErrorsIfAny(new UsernameValidator(updatedUserModel).validate());
    }

    private void validateName() {
        addErrorsIfAny(new NameValidator(updatedUserModel).validate());
    }

    private void validateBio() {
        addErrorsIfAny(new BioValidator(updatedUserModel).validate());
    }

    private void addErrorsIfAny(List<FieldValidationError> validationResult) {
        if (validationResult != null && !validationResult.isEmpty()) {
            fieldValidationErrors.addAll(validationResult);
        }
    }
}
