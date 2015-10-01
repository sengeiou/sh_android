package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.api.entity.mapper.UserApiEntityMapper;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.task.events.profile.UpdateUserProfileEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.validation.FieldValidationErrorEvent;
import com.shootr.android.task.validation.profile.BioValidator;
import com.shootr.android.task.validation.profile.NameValidator;
import com.shootr.android.task.validation.profile.UsernameValidator;
import com.shootr.android.task.validation.profile.WebsiteValidator;
import com.shootr.android.ui.model.UserModel;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
      @Main Bus bus,
      NetworkUtil networkUtil,
      SessionRepository sessionRepository,
      UserManager userManager,
      TimeUtils timeUtils,
      UserEntityMapper userEntityMapper,
      UserApiService userApiService,
      UserApiEntityMapper userApiEntityMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
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

    @Override protected void run() throws SQLException, IOException, Exception {
        localValidation();
        if (hasLocalErrors()) {
            postValidationErrors();
            return;
        }

        try {
            sendUpdatedProfileToServer();
            postSuccessfulEvent(new UpdateUserProfileEvent(null));
        } catch (ShootrServerException serverException) {
            ShootrError shootrError = serverException.getShootrError();
            FieldValidationError fieldValidationError = fieldErrorFromServer(shootrError.getErrorCode());
            if (fieldValidationError != null) {
                fieldValidationErrors.add(fieldValidationError);
                postValidationErrors();
            } else {
                throw serverException;
            }
        }
    }

    private boolean hasLocalErrors() {
        return !fieldValidationErrors.isEmpty();
    }

    private void postValidationErrors() {
        postCustomEvent(new FieldValidationErrorEvent(fieldValidationErrors));
    }

    private FieldValidationError fieldErrorFromServer(String errorCode) {
        int field = 0;
        if (isUsernameError(errorCode)) {
            field = FieldValidationError.FIELD_USERNAME;
        } else if (isNameError(errorCode)) {
            field = FieldValidationError.FIELD_NAME;
        } else if (isWebsiteError(errorCode)) {
               field = FieldValidationError.FIELD_WEBSITE;
        }else if (isBioError(errorCode)) {
            field = FieldValidationError.FIELD_BIO;

        }

        if (field > 0) {
            return new FieldValidationError(errorCode, field);
        } else {
            return null;
        }
    }

    private boolean isUsernameError(String errorCode) {
        Set<String> usernameCodes = new HashSet<>();
        usernameCodes.add(ShootrError.ERROR_CODE_USERNAME_DUPLICATE);
        usernameCodes.add(ShootrError.ERROR_CODE_USERNAME_NULL);
        usernameCodes.add(ShootrError.ERROR_CODE_USERNAME_TOO_SHORT);
        usernameCodes.add(ShootrError.ERROR_CODE_USERNAME_TOO_LONG);
        usernameCodes.add(ShootrError.ERROR_CODE_USERNAME_INVALID_CHARACTERS);
        return usernameCodes.contains(errorCode);
    }

    private boolean isNameError(String errorCode) {
        Set<String> nameCodes = new HashSet<>();
        nameCodes.add(ShootrError.ERROR_CODE_NAME_TOO_LONG);
        nameCodes.add(ShootrError.ERROR_CODE_NAME_TOO_SHORT);
        nameCodes.add(ShootrError.ERROR_CODE_NAME_INVALID_CHARACTERS);
        return nameCodes.contains(errorCode);
    }

    private boolean isWebsiteError(String errorCode) {
        Set<String> websiteCodes = new HashSet<>();
        websiteCodes.add(ShootrError.ERROR_CODE_WEBSITE_TOO_LONG);
        websiteCodes.add(ShootrError.ERROR_CODE_WEBSITE_WRONG_URI);
        return websiteCodes.contains(errorCode);
    }

    private boolean isBioError(String errorCode) {
        Set<String> bioCodes = new HashSet<>();
        bioCodes.add(ShootrError.ERROR_CODE_BIO_TOO_LONG);
        bioCodes.add(ShootrError.ERROR_CODE_BIO_TOO_SHORT);
        return bioCodes.contains(errorCode);
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

    private void sendUpdatedProfileToServer() throws IOException {
        UserEntity updatedUserEntity = updateEntityWithValues(updatedUserModel);

        updatedUserEntity.setModified(timeUtils.getCurrentDate());
        updatedUserEntity.setRevision(updatedUserEntity.getRevision() + 1);

        try {
            updatedUserEntity = userApiService.putUser(userApiEntityMapper.transform(updatedUserEntity));
            userManager.saveUser(updatedUserEntity);
            sessionRepository.setCurrentUser(userEntityMapper.transform(updatedUserEntity, updatedUserEntity.getIdUser()));
        } catch (ApiException e) {
            throw new IOException(e);
        }
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

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
