package com.shootr.android.task.jobs.shots;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.validation.FieldValidationErrorEvent;
import com.shootr.android.task.validation.shot.DuplicatedValidator;
import com.squareup.otto.Bus;
import com.shootr.android.data.entity.ShotEntity;
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
    private final SessionRepository sessionRepository;
    private final ShotManager shotManager;
    private final WatchManager watchManager;
    private final EventManager eventManager;

    private String comment;
    private String imageUrl;
    private final List<FieldValidationError> fieldValidationErrors;

    @Inject public PostNewShotJob(Application application, NetworkUtil networkUtil, @Main Bus bus,
      ShootrService service, SessionRepository sessionRepository, ShotManager shotManager, WatchManager watchManager,
      EventManager eventManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.sessionRepository = sessionRepository;
        this.shotManager = shotManager;
        this.watchManager = watchManager;
        this.eventManager = eventManager;
        fieldValidationErrors = new ArrayList<>();
    }

    public void init(String comment, String imageUrl) {
        this.comment = comment;
        this.imageUrl = imageUrl;
    }

    @Override
    protected void run() throws SQLException, IOException {
        if (isShotValid()) {
            ShotEntity shotTemplate = createShotEntity();
            ShotEntity postedShot = service.postNewShotWithImage(shotTemplate);
            postSuccessfulEvent(new PostNewShotResultEvent(postedShot));
        } else {
            postValidationErrors();
        }
    }

    private ShotEntity createShotEntity() {
        ShotEntity shotTemplate = new ShotEntity();
        shotTemplate.setComment(comment);
        shotTemplate.setIdUser(sessionRepository.getCurrentUserId());
        shotTemplate.setImage(imageUrl);
        appendEventData(shotTemplate);
        return shotTemplate;
    }

    private void appendEventData(ShotEntity shotTemplate) {
        WatchEntity watching = watchManager.getWatchVisibleByUser(sessionRepository.getCurrentUserId());
        if (watching != null) {
            EventEntity event = eventManager.getEventById(watching.getIdEvent());
            if (event != null) {
                shotTemplate.setIdEvent(event.getIdEvent());
                shotTemplate.setEventTag(event.getTag());
                shotTemplate.setEventTitle(event.getTitle());
            }
        }
    }

    private boolean isShotValid() {
        localValidation();
        return fieldValidationErrors.isEmpty();
    }

    private void localValidation() {
        validateComment();
        validateImage();
        validateShotDuplication();
    }

    private void validateImage() {
        if (imageUrl != null && imageUrl.isEmpty()) {
            imageUrl = null;
        }
    }

    private void validateComment() {
        if (comment != null && comment.isEmpty()) {
            comment = null;
        }
    }

    private void validateShotDuplication() {
        addErrorsIfAny(new DuplicatedValidator(getPreviousShot(), comment, imageUrl).validate());
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
        return shotManager.retrieveLastShotFromUser(sessionRepository.getCurrentUserId());
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
