package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.UserEntity;
import com.shootr.android.service.PhotoService;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.profile.UploadProfilePhotoEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.util.ImageResizer;
import com.shootr.android.util.TimeUtils;
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
    private final UserModelMapper userModelMapper;
    private final TimeUtils timeUtils;

    private File photoFile;

    @Inject public UploadProfilePhotoJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService shootrService, PhotoService photoService,
      UserManager userManager, SessionManager sessionManager, ImageResizer imageResizer,
      UserModelMapper userModelMapper, TimeUtils timeUtils) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.shootrService = shootrService;
        this.photoService = photoService;
        this.userManager = userManager;
        this.sessionManager = sessionManager;
        this.imageResizer = imageResizer;
        this.userModelMapper = userModelMapper;
        this.timeUtils = timeUtils;
    }

    public void init(File photoFile) {
        this.photoFile = photoFile;
    }

    @Override protected void run() throws SQLException, IOException, JSONException {
        File imageFile = getResizedImage(photoFile);
        String photoUrl = uploadPhoto(imageFile);
        UserEntity updatedUser = setCurrentUserPhoto(photoUrl);

        postSuccessfulEvent(new UploadProfilePhotoEvent(userModelMapper.toUserModel(updatedUser, null, true)));
    }

    private UserEntity setCurrentUserPhoto(String photoUrl) throws IOException {
        UserEntity currentUser = sessionManager.getCurrentUser();
        currentUser.setPhoto(photoUrl);
        currentUser.setCsysModified(timeUtils.getCurrentDate());
        userManager.saveUser(currentUser);
        currentUser = shootrService.saveUserProfile(currentUser);
        userManager.saveUser(currentUser);
        return currentUser;
    }

    private String uploadPhoto(File imageFile) throws IOException, JSONException {
        return photoService.uploadProfilePhotoAndGetUrl(imageFile);
    }

    private File getResizedImage(File newPhotoFile) throws IOException {
        return imageResizer.getResizedCroppedImageFile(newPhotoFile);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
