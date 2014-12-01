package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.profile.UpdateUserProfileEvent;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.validation.FieldValidationErrorEvent;
import com.shootr.android.task.validation.NameValidator;
import com.shootr.android.task.validation.UsernameValidator;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.util.TimeUtils;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UpdateUserProfileJob extends ShootrBaseJob<UpdateUserProfileEvent>{

    private static final int PRIORITY = 8;

    private ShootrService service;
    private SessionManager sessionManager;
    private UserManager userManager;
    private TimeUtils timeUtils;

    private UserModel updatedUserModel;
    private final List<FieldValidationError> fieldValidationErrors;

    @Inject public UpdateUserProfileJob(Application application, Bus bus, NetworkUtil networkUtil,
      ShootrService service, SessionManager sessionManager, UserManager userManager, TimeUtils timeUtils) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.sessionManager = sessionManager;
        this.userManager = userManager;
        this.timeUtils = timeUtils;
        this.fieldValidationErrors = new ArrayList<>();
    }

    //TODO domain logic should not know about view models
    public void init(UserModel updatedUserModel) {
        this.updatedUserModel = updatedUserModel;
    }

    @Override protected void run() throws SQLException, IOException, Exception {
        localValidation();
        boolean hasLocalErrors = !fieldValidationErrors.isEmpty();
        if (hasLocalErrors) {
            postCustomEvent(new FieldValidationErrorEvent(fieldValidationErrors));
            return;
        }
        sendUpdatedProfileToServer();
        //TODO catch remote validation
        postSuccessfulEvent(new UpdateUserProfileEvent(null));
    }

    private UserEntity updateEntityWithValues(UserModel updatedUserModel) {
        UserEntity updatedUserEntity = sessionManager.getCurrentUser().clone();
        updatedUserEntity.setUserName(updatedUserModel.getUsername());
        updatedUserEntity.setName(updatedUserModel.getName());
        return updatedUserEntity;
    }

    private void sendUpdatedProfileToServer() throws IOException {
        UserEntity updatedUserEntity = updateEntityWithValues(updatedUserModel);

        updatedUserEntity.setCsysModified(timeUtils.getCurrentDate());
        updatedUserEntity.setCsysRevision(updatedUserEntity.getCsysRevision()+1);

        updatedUserEntity = service.saveUserProfile(updatedUserEntity);
        userManager.saveUser(updatedUserEntity);
        sessionManager.setCurrentUser(updatedUserEntity);
    }

    private void localValidation() {
        validateUsername();
        validateName();
    }

    private void validateUsername() {
        addErrorsIfAny(new UsernameValidator(updatedUserModel).validate());
    }

    private void validateName() {
        addErrorsIfAny(new NameValidator(updatedUserModel).validate());
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
