package com.shootr.android.task.jobs.shots;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.service.PhotoService;
import com.shootr.android.task.events.profile.UploadShotImageEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.util.ImageResizer;
import com.squareup.otto.Bus;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import org.json.JSONException;

public class UploadShotImageJob extends ShootrBaseJob<UploadShotImageEvent> {

    private static final int PRIORITY = 5;
    private final PhotoService photoService;
    private final ImageResizer imageResizer;

    private File photoFile;

    @Inject public UploadShotImageJob(Application application, Bus bus, NetworkUtil networkUtil, PhotoService photoService,
      ImageResizer imageResizer) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.photoService = photoService;
        this.imageResizer = imageResizer;
    }

    public void init(File photoFile) {
        this.photoFile = photoFile;
    }

    @Override protected void run() throws SQLException, IOException, JSONException {
        File imageFile = getResizedImage(photoFile);
        String imageUrl = uploadPhoto(imageFile);

        postSuccessfulEvent(new UploadShotImageEvent(imageUrl));
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
