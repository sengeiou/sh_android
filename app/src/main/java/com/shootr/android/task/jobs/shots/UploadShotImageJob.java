package com.shootr.android.task.jobs.shots;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.service.PhotoService;
import com.shootr.android.service.ShootrServerException;
import com.shootr.android.task.events.profile.UploadShotImageEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.validation.FieldValidationErrorEvent;
import com.shootr.android.util.ImageResizer;
import com.squareup.otto.Bus;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.json.JSONException;

public class UploadShotImageJob extends ShootrBaseJob<UploadShotImageEvent> {

    private static final int PRIORITY = 5;
    private final PhotoService photoService;
    private final ImageResizer imageResizer;

    private File photoFile;

    @Inject public UploadShotImageJob(Application application, Bus bus, NetworkUtil networkUtil,
      PhotoService photoService, ImageResizer imageResizer) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.photoService = photoService;
        this.imageResizer = imageResizer;
    }

    public void init(File photoFile) {
        this.photoFile = photoFile;
    }

    @Override protected void run() throws SQLException, IOException, JSONException {
        try {
            File imageFile = getResizedImage(photoFile);
            String imageUrl = uploadPhoto(imageFile);
            postSuccessfulEvent(new UploadShotImageEvent(imageUrl));
        } catch (ShootrServerException serverException) {
            ShootrError shootrError = serverException.getShootrError();
            FieldValidationError fieldValidationError = fieldErrorFromServer(shootrError.getErrorCode());
            if (fieldValidationError != null) {
                postValidationError(fieldValidationError);
            } else {
                throw serverException;
            }
        }
    }

    private void postValidationError(FieldValidationError fieldValidationError) {
        List<FieldValidationError> errorsListOfOneElement = Arrays.asList(fieldValidationError);
        postCustomEvent(new FieldValidationErrorEvent(errorsListOfOneElement));
    }

    private FieldValidationError fieldErrorFromServer(String errorCode) {
        boolean isImageError = errorCode.equals(ShootrError.ERROR_CODE_INVALID_IMAGE);
        if (isImageError) {
            return new FieldValidationError(errorCode, FieldValidationError.FIELD_IMAGE);
        } else {
            return null;
        }
    }

    private String uploadPhoto(File imageFile) throws IOException, JSONException {
        return photoService.uploadShotImageAndGetUrl(imageFile);
    }

    private File getResizedImage(File newPhotoFile) throws IOException {
        return imageResizer.getResizedImageFile(newPhotoFile);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
