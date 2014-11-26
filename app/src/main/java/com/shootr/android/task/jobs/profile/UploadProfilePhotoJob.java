package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.PhotoService;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.profile.UploadProfilePhotoEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.util.ImageResizer;
import com.squareup.otto.Bus;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import org.json.JSONException;

public class UploadProfilePhotoJob extends ShootrBaseJob<UploadProfilePhotoEvent> {

    private static final int PRIORITY = 5;
    private final ShootrService shootrService;
    private final PhotoService photoService;
    private final UserManager userManager;
    private final SessionManager sessionManager;
    private final ImageResizer imageResizer;

    private File photoFile;

    @Inject public UploadProfilePhotoJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService shootrService, PhotoService photoService,
      UserManager userManager, SessionManager sessionManager, ImageResizer imageResizer) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.shootrService = shootrService;
        this.photoService = photoService;
        this.userManager = userManager;
        this.sessionManager = sessionManager;
        this.imageResizer = imageResizer;
    }

    public void init(File photoFile) {
        this.photoFile = photoFile;
    }

    @Override protected void run() throws SQLException, IOException, JSONException {
        File imageFile = getResizedImage(photoFile);

        String photoUrl = uploadPhoto(imageFile);
        setCurrentUserPhoto(photoUrl);
    }

    private void setCurrentUserPhoto(String photoUrl) throws IOException {
        UserEntity currentUser = sessionManager.getCurrentUser();
        currentUser.setPhoto(photoUrl);
        userManager.saveUser(currentUser);
        shootrService.saveUserProfile(currentUser);
    }

    private String uploadPhoto(File imageFile) throws IOException, JSONException {
        return photoService.uploadPhotoAndGetUrl(imageFile);
    }

    private File getResizedImage(File newPhotoFile) throws IOException {
        return imageResizer.getResizedCroppedImageFile(newPhotoFile);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
