package com.shootr.android.task.jobs.shots;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.validation.FieldValidationErrorEvent;
import com.shootr.android.task.validation.shot.DuplicatedValidator;
import com.squareup.otto.Bus;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.shots.PostNewShotResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PostNewShotJob extends ShootrBaseJob<PostNewShotResultEvent> {

    private static final int PRIORITY = 5;

    private final ShootrService service;
    private final SessionManager sessionManager;
    private final ShotManager shotManager;

    private String comment;
    private String imageUrl;
    private final List<FieldValidationError> fieldValidationErrors;

    @Inject public PostNewShotJob(Application application, NetworkUtil networkUtil, Bus bus, ShootrService service,
      SessionManager sessionManager, ShotManager shotManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.sessionManager = sessionManager;
        this.shotManager = shotManager;
        fieldValidationErrors = new ArrayList<>();
    }

    public void init(String comment, String imageUrl){
        this.comment = comment;
        this.imageUrl = imageUrl;
    }

    @Override
    protected void run() throws SQLException, IOException {
        if (isShotValid()) {
            ShotEntity postedShot = service.postNewShotWithImage(sessionManager.getCurrentUserId(), comment, imageUrl);
            postSuccessfulEvent(new PostNewShotResultEvent(postedShot));
        } else {
            postValidationErrors();
        }
    }

    private boolean isShotValid() {
        localValidation();
        return fieldValidationErrors.isEmpty();
    }

    private void localValidation() {
        validateShotDuplication();
    }

    private void validateShotDuplication() {
        addErrorsIfAny(new DuplicatedValidator(getPreviousShot(), comment).validate());
    }

    private void addErrorsIfAny(List<FieldValidationError> validationResult) {
        if (validationResult != null && !validationResult.isEmpty()) {
            fieldValidationErrors.addAll(validationResult);
        }
    }

    private void postValidationErrors() {
        postCustomEvent(new FieldValidationErrorEvent(fieldValidationErrors));
    }

    private ShotEntity getPreviousShot() {
        return shotManager.retrieveLastShotFromUser(sessionManager.getCurrentUserId());
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }

}
