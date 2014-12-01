package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.validation.FieldValidationErrorEvent;
import com.shootr.android.task.validation.UsernameValidator;
import com.shootr.android.ui.model.UserModel;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UpdateUserProfileJob extends ShootrBaseJob<Void>{

    private static final int PRIORITY = 8;

    private UserModel updatedUserModel;
    private List<FieldValidationError> fieldValidationErrors;

    @Inject public UpdateUserProfileJob(Application application, Bus bus, NetworkUtil networkUtil) {
        super(new Params(PRIORITY), application, bus, networkUtil);
    }

    public void init(UserModel updatedUserModel) {
        this.updatedUserModel = updatedUserModel;
        this.fieldValidationErrors = new ArrayList<>();
    }

    @Override protected void run() throws SQLException, IOException, Exception {
        localValidation();
        boolean hasLocalErrors = !fieldValidationErrors.isEmpty();
        if (hasLocalErrors) {
            postCustomEvent(new FieldValidationErrorEvent(fieldValidationErrors));
            return;
        }
        //TODO send profile
        //TODO catch remote validation
    }

    private void localValidation() {
        validateUsername();
    }

    private void validateUsername() {
        List<FieldValidationError> usernameErrors = new UsernameValidator(updatedUserModel).validate();
        if (usernameErrors != null && !usernameErrors.isEmpty()) {
            fieldValidationErrors.addAll(usernameErrors);
        }
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }


}
